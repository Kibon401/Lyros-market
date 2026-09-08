package com.example.lyrosmarket.presentation.admin.products

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lyrosmarket.core.Resource
import com.example.lyrosmarket.data.remote.dto.ProductDto
import com.example.lyrosmarket.data.remote.dto.ProductRequest
import com.example.lyrosmarket.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class AdminProductsState(
    val products: List<ProductDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUploadingImage: Boolean = false
)

@HiltViewModel
class AdminProductsViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = mutableStateOf(AdminProductsState())
    val state: State<AdminProductsState> = _state

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadInventory()
    }

    fun loadInventory() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = adminRepository.viewInventory()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        products = result.data ?: emptyList(),
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "An unexpected error occurred",
                        isLoading = false
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    fun addProduct(
        name: String,
        description: String,
        price: Double,
        categoryId: Int,
        stockQuantity: Int,
        isHighDemand: Boolean,
        imageFile: File?
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            var uploadedImageUrl = ""
            if (imageFile != null) {
                _state.value = _state.value.copy(isUploadingImage = true)
                when (val imageResult = adminRepository.uploadProductImage(imageFile)) {
                    is Resource.Success -> {
                        uploadedImageUrl = imageResult.data ?: ""
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, isUploadingImage = false)
                        _uiEvent.emit("Image upload failed: ${imageResult.message}")
                        return@launch
                    }
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isUploadingImage = true)
                    }
                }
                _state.value = _state.value.copy(isUploadingImage = false)
            }

            val request = ProductRequest(
                name = name,
                description = description,
                price = price,
                categoryId = categoryId,
                stockQuantity = stockQuantity,
                imageUrl = uploadedImageUrl,
                isHighDemand = isHighDemand
            )

            when (val result = adminRepository.createProduct(request)) {
                is Resource.Success -> {
                    _uiEvent.emit("Product added successfully!")
                    loadInventory()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _uiEvent.emit(result.message ?: "Failed to add product")
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    fun updateProduct(
        productId: Int,
        name: String,
        description: String,
        price: Double,
        categoryId: Int,
        stockQuantity: Int,
        isHighDemand: Boolean,
        imageFile: File?,
        existingImageUrl: String
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            var imageUrlToUse = existingImageUrl
            if (imageFile != null) {
                _state.value = _state.value.copy(isUploadingImage = true)
                when (val imageResult = adminRepository.uploadProductImage(imageFile)) {
                    is Resource.Success -> {
                        imageUrlToUse = imageResult.data ?: existingImageUrl
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, isUploadingImage = false)
                        _uiEvent.emit("Image upload failed: ${imageResult.message}")
                        return@launch
                    }
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isUploadingImage = true)
                    }
                }
                _state.value = _state.value.copy(isUploadingImage = false)
            }

            val request = ProductRequest(
                name = name,
                description = description,
                price = price,
                categoryId = categoryId,
                stockQuantity = stockQuantity,
                imageUrl = imageUrlToUse,
                isHighDemand = isHighDemand
            )

            when (val result = adminRepository.updateProduct(productId, request)) {
                is Resource.Success -> {
                    _uiEvent.emit("Product updated successfully!")
                    loadInventory()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _uiEvent.emit(result.message ?: "Failed to update product")
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = adminRepository.deleteProduct(productId)) {
                is Resource.Success -> {
                    _uiEvent.emit("Product deleted successfully!")
                    loadInventory()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _uiEvent.emit(result.message ?: "Failed to delete product")
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }
}
