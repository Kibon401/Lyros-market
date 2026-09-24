package com.lyrosmarket.app.presentation.orders

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyrosmarket.app.core.Resource
import com.lyrosmarket.app.domain.model.Order
import com.lyrosmarket.app.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {

    private val _state = mutableStateOf(OrderDetailsState())
    val state: State<OrderDetailsState> = _state

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.getOrder(orderId)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        order = result.data
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun confirmDelivery(orderId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = repository.confirmDelivery(orderId)) {
                is Resource.Success -> {
                    loadOrder(orderId) // Reload order to reflect the new status
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to confirm delivery"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    data class OrderDetailsState(
        val order: Order? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
