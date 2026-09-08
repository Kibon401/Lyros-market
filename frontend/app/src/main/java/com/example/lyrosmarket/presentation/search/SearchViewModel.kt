package com.example.lyrosmarket.presentation.search

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
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _state = mutableStateOf(SearchState())
    val state: State<SearchState> = _state

    private var searchJob: Job? = null

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            when (val result = repository.getCategories()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        categories = result.data ?: emptyList()
                    )
                }
                else -> {
                    // Ignore errors for categories, just don't show them
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L) // Debounce search
            searchProducts(query, _state.value.selectedCategoryId)
        }
    }
    
    fun onCategorySelected(categoryId: Int?) {
        _state.value = _state.value.copy(selectedCategoryId = categoryId)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchProducts(_searchQuery.value, categoryId)
        }
    }

    fun onAddToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addProductToCart(product, 1)
        }
    }

    private fun searchProducts(query: String, categoryId: Int?) {
        if (query.isBlank() && categoryId == null) {
            _state.value = _state.value.copy(products = emptyList(), error = null)
            return
        }
        
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = repository.searchProducts(query, 1, 50, categoryId)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        products = result.data ?: emptyList(),
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

    data class SearchState(
        val isLoading: Boolean = false,
        val products: List<Product> = emptyList(),
        val categories: List<Category> = emptyList(),
        val selectedCategoryId: Int? = null,
        val error: String? = null
    )
}
