package com.example.lyrosmarket.presentation.admin.categories

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lyrosmarket.core.Resource
import com.example.lyrosmarket.data.remote.dto.CategoryRequest
import com.example.lyrosmarket.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.lyrosmarket.data.remote.dto.CategoryDto

data class AdminCategoriesState(
    val categories: List<CategoryDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AdminCategoriesViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = mutableStateOf(AdminCategoriesState())
    val state: State<AdminCategoriesState> = _state

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = adminRepository.viewCategories()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        categories = result.data ?: emptyList(),
                        isLoading = false
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

    fun createCategory(name: String, description: String, parentId: Int?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val request = CategoryRequest(name, description, parentId)
            when (val result = adminRepository.createCategory(request)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _uiEvent.emit("Category created successfully!")
                    loadCategories()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                    _uiEvent.emit(result.message ?: "Failed to create category")
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    fun updateCategory(categoryId: Int, name: String, description: String, parentId: Int?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val request = CategoryRequest(name, description, parentId)
            when (val result = adminRepository.updateCategory(categoryId, request)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _uiEvent.emit("Category updated successfully!")
                    loadCategories()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _uiEvent.emit(result.message ?: "Failed to update category")
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    fun deleteCategory(categoryId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = adminRepository.deleteCategory(categoryId)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _uiEvent.emit("Category deleted successfully")
                    loadCategories()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _uiEvent.emit(result.message ?: "Failed to delete category")
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }
}
