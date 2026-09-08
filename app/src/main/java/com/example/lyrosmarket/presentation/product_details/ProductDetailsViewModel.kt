package com.example.lyrosmarket.presentation.product_details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lyrosmarket.core.Resource
import com.example.lyrosmarket.domain.model.Product
import com.example.lyrosmarket.domain.repository.ProductRepository
import com.example.lyrosmarket.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val cartRepository: CartRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(ProductDetailsState())
    val state: State<ProductDetailsState> = _state

    init {
        savedStateHandle.get<String>("productId")?.let { productId ->
            getProduct(productId.toInt())
        }
    }

    fun onAddToCart(quantity: Int) {
        viewModelScope.launch {
            _state.value.product?.let { product ->
                cartRepository.addProductToCart(product, quantity)
            }
        }
    }

    private fun getProduct(productId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = repository.getProduct(productId)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        product = result.data,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "An unknown error occurred"
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    data class ProductDetailsState(
        val isLoading: Boolean = false,
        val product: Product? = null,
        val error: String? = null
    )
}
