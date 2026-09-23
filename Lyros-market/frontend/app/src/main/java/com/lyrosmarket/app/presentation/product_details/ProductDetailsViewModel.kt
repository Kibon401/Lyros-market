package com.lyrosmarket.app.presentation.product_details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyrosmarket.app.core.Resource
import com.lyrosmarket.app.domain.model.Product
import com.lyrosmarket.app.domain.repository.ProductRepository
import com.lyrosmarket.app.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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
                    result.data?.let { fetchRelatedProducts(it) }
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

    private fun fetchRelatedProducts(product: Product) {
        viewModelScope.launch {
            when (val result = repository.getProducts(page = 1, size = 50)) {
                is Resource.Success -> {
                    val allProducts = result.data ?: emptyList()
                    val related = allProducts
                        .filter { it.id != product.id }
                        .filter { it.categoryId == product.categoryId }
                    
                    val fallback = if (related.isEmpty()) {
                        allProducts.filter { it.id != product.id }.take(4)
                    } else {
                        related.take(4)
                    }
                    
                    _state.value = _state.value.copy(relatedProducts = fallback)
                }
                else -> {}
            }
        }
    }

    data class ProductDetailsState(
        val isLoading: Boolean = false,
        val product: Product? = null,
        val relatedProducts: List<Product> = emptyList(),
        val error: String? = null
    )
}
