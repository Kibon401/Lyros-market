package com.example.lyrosmarket.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lyrosmarket.core.SessionManager
import com.example.lyrosmarket.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _fullName = mutableStateOf(sessionManager.getUserName() ?: "User")
    val fullName: State<String> = _fullName

    private val _email = mutableStateOf(sessionManager.getUserEmail() ?: "user@example.com")
    val email: State<String> = _email

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    fun onFullNameChange(name: String) {
        _fullName.value = name
        sessionManager.saveUserName(name)
    }

    fun onEmailChange(email: String) {
        _email.value = email
        sessionManager.saveUserEmail(email)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _logoutEvent.emit(Unit)
        }
    }
}
