package com.lyrosmarket.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyrosmarket.app.core.SessionManager
import com.lyrosmarket.app.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val sessionExpiredEvent: SharedFlow<Unit> = sessionManager.sessionExpiredEvent

    val cartBadgeCount: StateFlow<Int> = cartRepository.cartItems
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
        
    val startDestination: String
        get() {
            return if (sessionManager.isLoggedIn() && sessionManager.isSessionValid()) {
                val role = sessionManager.getUserRole() ?: "USER"
                if (role.equals("ADMIN", ignoreCase = true)) {
                    Screen.AdminDashboard.route
                } else if (role.equals("DRIVER", ignoreCase = true)) {
                    Screen.DeliveryDashboard.route
                } else {
                    Screen.Home.route
                }
            } else {
                Screen.Login.route
            }
        }
        
    fun getSavedLocation(): Pair<Double, Double>? {
        return sessionManager.getLocation()
    }
}
