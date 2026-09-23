package com.lyrosmarket.app.presentation.checkout

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyrosmarket.app.core.Resource
import com.lyrosmarket.app.data.remote.dto.AddressDto
import com.lyrosmarket.app.domain.repository.PaymentRepository
import com.lyrosmarket.app.domain.repository.OrderRepository
import com.lyrosmarket.app.domain.repository.AuthRepository
import com.lyrosmarket.app.domain.repository.CartRepository
import com.lyrosmarket.app.core.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = mutableStateOf(CheckoutState())
    val state: State<CheckoutState> = _state

    init {
        loadAddresses()
        observeCart()
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.cartItems.collect { items ->
                val subtotal = items.sumOf { it.product.price * it.quantity }
                _state.value = _state.value.copy(
                    subtotal = subtotal,
                    total = subtotal + (_state.value.selectedAddress?.shippingFee ?: 0.0),
                    itemCount = items.sumOf { it.quantity }
                )
            }
        }
    }

    private fun loadAddresses() {
        val savedLoc = sessionManager.getLocation()
        if (savedLoc != null) {
            onMapLocationSelected(savedLoc.first, savedLoc.second)
            _state.value = _state.value.copy(isLoading = false)
        } else {
            _state.value = _state.value.copy(
                isLoading = false,
                selectedAddress = AddressDto(label = "Default", fullAddress = "Eldoret CBD", shippingFee = 150.0)
            )
        }
    }

    fun onAddressSelected(address: AddressDto) {
        _state.value = _state.value.copy(
            selectedAddress = address,
            total = _state.value.subtotal + address.shippingFee
        )
    }

    fun onPhoneNumberChange(number: String) {
        _state.value = _state.value.copy(phoneNumber = number)
    }

    fun onMapLocationSelected(lat: Double, lng: Double) {
        val storeLat = 0.5142
        val storeLng = 35.2697
        
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            storeLat, storeLng,
            lat, lng,
            results
        )
        val distanceInKm = results[0] / 1000.0
        
        // Base fee KSh 50 + KSh 20 per km
        val calculatedFee = 50.0 + (20.0 * distanceInKm)
        
        val newAddress = AddressDto(
            label = "Map Location",
            fullAddress = "Lat: ${String.format("%.4f", lat)}, Lng: ${String.format("%.4f", lng)}",
            shippingFee = calculatedFee,
            latitude = lat,
            longitude = lng
        )
        
        sessionManager.saveLocation(lat, lng)
        
        _state.value = _state.value.copy(
            selectedAddress = newAddress,
            total = _state.value.subtotal + calculatedFee
        )
    }

    fun initiatePayment(deliveryAddress: String) {
        val phoneNumber = _state.value.phoneNumber
        if (phoneNumber.length < 9) {
            _state.value = _state.value.copy(error = "Please enter a valid phone number")
            return
        }

        // Format number to 254... if needed
        val formattedNumber = if (phoneNumber.startsWith("0")) {
            "254" + phoneNumber.substring(1)
        } else if (phoneNumber.startsWith("7") || phoneNumber.startsWith("1")) {
            "254$phoneNumber"
        } else {
            phoneNumber
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            val lat = _state.value.selectedAddress?.latitude ?: 0.5142
            val lng = _state.value.selectedAddress?.longitude ?: 35.2697
            
            // 1. Checkout to get OrderId
            val checkoutResult = orderRepository.checkout(
                deliveryAddress = deliveryAddress,
                latitude = lat,
                longitude = lng
            )

            when (checkoutResult) {
                is Resource.Success -> {
                    val order = checkoutResult.data ?: return@launch
                    val orderId = order.id.toIntOrNull() ?: return@launch
                    
                    // We don't update total to order.totalAmount here to prevent
                    // the price from jumping on the screen if the server calculates
                    // it differently. We rely on the local calculation for the UI.
                    
                    // 2. Initiate M-Pesa STK Push
                    val paymentResult = paymentRepository.initiateStkPush(formattedNumber, orderId)
                    
                    when (paymentResult) {
                        is Resource.Success -> {
                            // STK push sent, now poll for payment status
                            _state.value = _state.value.copy(
                                message = paymentResult.data?.CustomerMessage ?: paymentResult.data?.message ?: "STK Push sent! Please enter your PIN."
                            )
                            pollPaymentStatus(orderId)
                        }
                        is Resource.Error -> {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                isPaymentFailed = true,
                                error = paymentResult.message ?: "Failed to initiate payment"
                            )
                        }
                        is Resource.Loading -> {}
                    }
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isPaymentFailed = true,
                        error = checkoutResult.message ?: "Checkout failed"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun pollPaymentStatus(orderId: Int) {
        viewModelScope.launch {
            var attempts = 0
            val maxAttempts = 20 // Poll for 60 seconds (3 seconds interval)
            while (attempts < maxAttempts) {
                kotlinx.coroutines.delay(3000)
                val orderResult = orderRepository.getOrder(orderId.toString())
                if (orderResult is Resource.Success) {
                    val status = orderResult.data?.status?.uppercase()
                    if (status == "PAID" || status == "COMPLETED") {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isPaymentInitiated = true, // We use this flag to trigger the success navigation
                            message = "Payment successful!"
                        )
                        return@launch
                    } else if (status == "CANCELLED" || status == "FAILED") {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isPaymentFailed = true,
                            error = "Payment failed or was cancelled."
                        )
                        return@launch
                    }
                }
                attempts++
            }
            
            // Timeout reached without getting a successful or failed status
            _state.value = _state.value.copy(
                isLoading = false,
                isPaymentFailed = true,
                error = "Payment confirmation timed out."
            )
        }
    }

    data class CheckoutState(
        val phoneNumber: String = "",
        val isLoading: Boolean = false,
        val isPaymentInitiated: Boolean = false,
        val isPaymentFailed: Boolean = false,
        val addresses: List<AddressDto> = emptyList(),
        val selectedAddress: AddressDto? = null,
        val subtotal: Double = 0.0,
        val total: Double = 0.0,
        val itemCount: Int = 0,
        val error: String? = null,
        val message: String? = null
    )
}
