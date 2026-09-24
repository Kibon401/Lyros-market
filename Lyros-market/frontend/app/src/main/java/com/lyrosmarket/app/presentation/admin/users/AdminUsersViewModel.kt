package com.lyrosmarket.app.presentation.admin.users

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyrosmarket.app.core.Resource
import com.lyrosmarket.app.data.remote.dto.AdminUserDto
import com.lyrosmarket.app.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUsersState(
    val users: List<AdminUserDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AdminUsersViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = mutableStateOf(AdminUsersState())
    val state: State<AdminUsersState> = _state

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = adminRepository.viewAllUsers()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        users = result.data ?: emptyList(),
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "Failed to load users",
                        isLoading = false
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    fun updateUserRole(userId: Int, newRole: String) {
        val user = _state.value.users.find { it.id == userId } ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val request = com.lyrosmarket.app.data.remote.dto.UpdateUserRequest(
                username = user.username,
                email = user.email,
                role = newRole
            )
            when (val result = adminRepository.updateUser(userId, request)) {
                is Resource.Success -> {
                    _uiEvent.emit("Role updated to $newRole")
                    loadUsers()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _uiEvent.emit(result.message ?: "Failed to update role")
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = adminRepository.deleteUser(userId)) {
                is Resource.Success -> {
                    _uiEvent.emit("User deleted successfully")
                    loadUsers()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _uiEvent.emit(result.message ?: "Failed to delete user")
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }
}
