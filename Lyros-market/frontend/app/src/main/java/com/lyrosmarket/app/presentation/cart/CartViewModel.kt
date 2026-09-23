package com.lyrosmarket.app.presentation.cart

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyrosmarket.app.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.lyrosmarket.app.core.SessionManager
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = mutableStateOf(CartState())
    val state: State<CartState> = _state

    init {
        val savedLoc = sessionManager.getLocation()
        if (savedLoc != null) {
            onMapLocationSelected(savedLoc.first, savedLoc.second)
        }
        
        viewModelScope.launch {
            cartRepository.cartItems.collectLatest { items ->
                _state.value = _state.value.copy(cartItems = items)
            }
        }
    }

    fun onQuantityChange(productId: Int, delta: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(productId, delta)
        }
    }

    fun removeItem(productId: Int) {
        viewModelScope.launch {
            cartRepository.removeProductFromCart(productId)
        }
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
        
        val newAddress = com.lyrosmarket.app.data.remote.dto.AddressDto(
            label = "Map Location",
            fullAddress = "Lat: ${String.format("%.4f", lat)}, Lng: ${String.format("%.4f", lng)}",
            shippingFee = calculatedFee,
            latitude = lat,
            longitude = lng
        )
        
        sessionManager.saveLocation(lat, lng)
        
        _state.value = _state.value.copy(
            selectedAddress = newAddress,
            deliveryFee = calculatedFee
        )
    }
}
