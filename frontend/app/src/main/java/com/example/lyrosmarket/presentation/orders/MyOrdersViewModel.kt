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
class MyOrdersViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {

    private val _state = mutableStateOf(MyOrdersState())
    val state: State<MyOrdersState> = _state

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.getOrders()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        orders = result.data ?: emptyList()
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    data class MyOrdersState(
        val orders: List<Order> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
