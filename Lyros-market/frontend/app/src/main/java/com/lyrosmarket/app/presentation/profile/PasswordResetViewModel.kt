package com.lyrosmarket.app.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyrosmarket.app.core.Resource
import com.lyrosmarket.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordResetViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf(PasswordResetState())
    val state: State<PasswordResetState> = _state

    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun onCodeChange(code: String) {
        _state.value = _state.value.copy(code = code)
    }

    fun onNewPasswordChange(password: String) {
        _state.value = _state.value.copy(newPassword = password)
    }

    fun onResetPassword() {
        if (_state.value.email.isBlank() || _state.value.code.isBlank() || _state.value.newPassword.isBlank()) {
            _state.value = _state.value.copy(error = "Please fill all fields")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = authRepository.resetPassword(
                _state.value.email,
                _state.value.code,
                _state.value.newPassword
            )
            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to reset password"
                    )
                }
                is Resource.Loading -> Unit
            }
        }
    }
    fun onRequestCode() {
        if (_state.value.email.isBlank()) {
            _state.value = _state.value.copy(error = "Please enter your email first")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isSendingCode = true, error = null, successMessage = null)
            val result = authRepository.forgotPassword(_state.value.email)
            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isSendingCode = false, 
                        isCodeSent = true,
                        successMessage = "Reset code sent to your email"
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isSendingCode = false,
                        error = result.message ?: "Failed to send code"
                    )
                }
                is Resource.Loading -> Unit
            }
        }
    }
}

data class PasswordResetState(
    val email: String = "",
    val code: String = "",
    val newPassword: String = "",
    val isLoading: Boolean = false,
    val isSendingCode: Boolean = false,
    val isCodeSent: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val isSuccess: Boolean = false
)
