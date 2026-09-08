package com.example.lyrosmarket.presentation.admin.orders

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lyrosmarket.core.Resource
import com.example.lyrosmarket.data.remote.dto.OrderDto
import com.example.lyrosmarket.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminOrdersState(
    val orders: List<OrderDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AdminOrdersViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = mutableStateOf(AdminOrdersState())
    val state: State<AdminOrdersState> = _state

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = adminRepository.viewAllOrders()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        orders = result.data ?: emptyList(),
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "Failed to load orders",
                        isLoading = false
                    )
                }
                is Resource.Loading<*> -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    fun markOrderPaid(orderId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = adminRepository.markOrderPaid(orderId)) {
                is Resource.Success -> {
                    _uiEvent.emit("Order marked as PAID")
                    loadOrders()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _uiEvent.emit(result.message ?: "Failed to mark order as paid")
                }
                is Resource.Loading<*> -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }
}
