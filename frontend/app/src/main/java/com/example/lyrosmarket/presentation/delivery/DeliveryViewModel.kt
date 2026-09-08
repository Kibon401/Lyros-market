package com.example.lyrosmarket.presentation.delivery

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lyrosmarket.core.Resource
import com.example.lyrosmarket.domain.model.DeliveryOrder
import com.example.lyrosmarket.domain.repository.AuthRepository
import com.example.lyrosmarket.domain.repository.DeliveryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class DeliveryViewModel @Inject constructor(
    private val repository: DeliveryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf(DeliveryState())
    val state: State<DeliveryState> = _state

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            val availableResult = repository.getAvailableOrders()
            val myOrdersResult = repository.getMyOrders()

            if (availableResult is Resource.Error) {
                _state.value = _state.value.copy(isLoading = false, error = availableResult.message)
                return@launch
            }

            _state.value = _state.value.copy(
                isLoading = false,
                availableOrders = availableResult.data ?: emptyList(),
                myOrders = myOrdersResult.data ?: emptyList()
            )
        }
    }

    fun acceptOrder(orderId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = repository.acceptOrder(orderId)
            if (result is Resource.Success) {
                loadOrders() // Refresh
            } else {
                _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun updateStatus(orderId: String, status: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = repository.updateStatus(orderId, status)
            if (result is Resource.Success) {
                loadOrders() // Refresh
            } else {
                _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    data class DeliveryState(
        val isLoading: Boolean = false,
        val availableOrders: List<DeliveryOrder> = emptyList(),
        val myOrders: List<DeliveryOrder> = emptyList(),
        val error: String? = null
    )
}
