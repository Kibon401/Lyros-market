package com.example.lyrosmarket.presentation.orders

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lyrosmarket.core.Resource
import com.example.lyrosmarket.domain.model.Order
import com.example.lyrosmarket.domain.repository.OrderRepository
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

    data class OrderDetailsState(
        val order: Order? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
