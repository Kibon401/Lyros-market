package com.lyrosmarket.app.presentation.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyrosmarket.app.core.Resource
import com.lyrosmarket.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import jakarta.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun login() {
        val trimmedEmail = _email.value.trim()
        val trimmedPassword = _password.value.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Please enter both email and password."))
            }
            return
        }

        if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Please enter a valid email address."))
            }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.login(trimmedEmail, trimmedPassword)
            _isLoading.value = false

            when (result) {
                is Resource.Success -> {
                    val role = result.data?.role ?: "USER"
                    android.util.Log.d("LoginViewModel", "Login successful! Role received: '$role'")
                    _eventFlow.emit(UiEvent.LoginSuccess(role))
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Authentication failed. Please check your credentials."))
                }
                is Resource.Loading -> {}
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data class LoginSuccess(val role: String) : UiEvent()
    }
}
