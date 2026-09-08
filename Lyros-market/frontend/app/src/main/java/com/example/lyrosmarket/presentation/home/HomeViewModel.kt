package com.example.lyrosmarket.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lyrosmarket.core.Resource
import com.example.lyrosmarket.domain.model.Category
import com.example.lyrosmarket.domain.model.Product
import com.example.lyrosmarket.domain.repository.ProductRepository
import com.example.lyrosmarket.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import jakarta.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private var searchJob: Job? = null

    init {
        loadHomeData()
        observeCart()
    }

    fun onCategorySelected(categoryId: Int?) {
        _state.value = _state.value.copy(selectedCategoryId = categoryId)
        loadProducts(categoryId, _state.value.searchQuery)
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            loadProducts(_state.value.selectedCategoryId, query)
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.cartItems.collect { items ->
                _state.value = _state.value.copy(cartBadgeCount = items.sumOf { it.quantity })
            }
        }
    }

    fun onAddToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addProductToCart(product, 1)
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val categoriesResult = repository.getCategories()
            val productsResult = repository.getProducts(1, 20, _state.value.selectedCategoryId)
            val highDemandResult = repository.getHighDemandProducts(1, 10)

            _state.value = _state.value.copy(
                isLoading = false,
                categories = if (categoriesResult is Resource.Success) categoriesResult.data ?: emptyList() else emptyList(),
                products = if (productsResult is Resource.Success) productsResult.data ?: emptyList() else emptyList(),
                highDemandProducts = if (highDemandResult is Resource.Success) highDemandResult.data ?: emptyList() else emptyList(),
                error = if (productsResult is Resource.Error) productsResult.message else null
            )
        }
    }

    private fun loadProducts(categoryId: Int?, query: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val result = if (query.isEmpty()) {
                repository.getProducts(1, 20, categoryId)
            } else {
                repository.searchProducts(query, 1, 20)
            }

            _state.value = _state.value.copy(
                isLoading = false,
                products = if (result is Resource.Success) result.data ?: emptyList() else emptyList(),
                error = if (result is Resource.Error) result.message else null
            )
        }
    }

    data class HomeState(
        val isLoading: Boolean = false,
        val products: List<Product> = emptyList(),
        val categories: List<Category> = emptyList(),
        val highDemandProducts: List<Product> = emptyList(),
        val selectedCategoryId: Int? = null,
        val searchQuery: String = "",
        val cartBadgeCount: Int = 0,
        val error: String? = null
    )
}
