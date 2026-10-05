package com.lyrosmarket.app.presentation.register

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
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _username = mutableStateOf("")
    val username: State<String> = _username

    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onUsernameChange(username: String) {
        _username.value = username
    }

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun register() {
        val trimmedUsername = _username.value.trim()
        val trimmedEmail = _email.value.trim()
        val trimmedPassword = _password.value.trim()

        if (trimmedUsername.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Please enter a username."))
            }
            return
        }

        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Please enter a valid email address."))
            }
            return
        }

        if (trimmedPassword.length < 6) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Password must be at least 6 characters long."))
            }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.register(trimmedUsername, trimmedEmail, trimmedPassword)
            _isLoading.value = false

            when (result) {
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.RegisterSuccess)
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Registration failed. Please try again."))
                }
                is Resource.Loading -> {}
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object RegisterSuccess : UiEvent()
    }
}
