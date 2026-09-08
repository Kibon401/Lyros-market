package com.example.lyrosmarket.presentation.cart

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lyrosmarket.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = mutableStateOf(CartState())
    val state: State<CartState> = _state

    init {
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
}
