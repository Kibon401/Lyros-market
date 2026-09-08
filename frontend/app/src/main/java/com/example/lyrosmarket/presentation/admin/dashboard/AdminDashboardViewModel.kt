package com.example.lyrosmarket.presentation.admin.dashboard

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lyrosmarket.core.Resource
import com.example.lyrosmarket.data.remote.dto.AdminDashboardDto
import com.example.lyrosmarket.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class TopClient(
    val username: String,
    val totalSpent: Double
)

data class FinancialReports(
    val daily: Double = 0.0,
    val weekly: Double = 0.0,
    val monthly: Double = 0.0,
    val yearly: Double = 0.0,
    val q1: Double = 0.0,
    val q2: Double = 0.0,
    val q3: Double = 0.0,
    val q4: Double = 0.0
)

data class AdminDashboardState(
    val dashboardData: AdminDashboardDto? = null,
    val topClients: List<TopClient> = emptyList(),
    val reports: FinancialReports = FinancialReports(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = mutableStateOf(AdminDashboardState())
    val state: State<AdminDashboardState> = _state

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            try {
                var computedRevenue = 0.0
                var topClients = emptyList<TopClient>()
                var financialReports = FinancialReports()
                
                // Fetch orders first to calculate real revenue, clients, and reports
                val ordersResult = adminRepository.viewAllOrders()
                val usersResult = adminRepository.viewAllUsers()

                if (ordersResult is Resource.Success && usersResult is Resource.Success) {
                    val orders = ordersResult.data ?: emptyList()
                    val users = usersResult.data ?: emptyList()
                    
                    val validOrders = orders.filter { it.status != "CANCELLED" }
                    computedRevenue = validOrders.sumOf { it.totalAmount }

                    // Compute Top 5 Clients
                    val clientSpending = validOrders.groupBy { it.userId }
                        .mapValues { entry -> entry.value.sumOf { it.totalAmount } }
                    
                    topClients = clientSpending.mapNotNull { (userId, spent) ->
                        val user = users.find { it.id == userId }
                        if (user != null) TopClient(user.username, spent) else null
                    }.sortedByDescending { it.totalSpent }.take(5)

                    // Compute Financial Reports
                    val now = LocalDateTime.now()
                    var daily = 0.0
                    var weekly = 0.0
                    var monthly = 0.0
                    var yearly = 0.0
                    var q1 = 0.0
                    var q2 = 0.0
                    var q3 = 0.0
                    var q4 = 0.0

                    validOrders.forEach { order ->
                        try {
                            val orderDate = LocalDateTime.parse(order.orderDate, DateTimeFormatter.ISO_DATE_TIME)
                            val amount = order.totalAmount
                            
                            if (orderDate.year == now.year) {
                                yearly += amount
                                if (orderDate.monthValue == now.monthValue) {
                                    monthly += amount
                                    if (orderDate.dayOfMonth == now.dayOfMonth) {
                                        daily += amount
                                    }
                                }
                                
                                // Check if within the last 7 days
                                val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(orderDate, now)
                                if (daysBetween in 0..7) {
                                    weekly += amount
                                }
                                
                                when (orderDate.monthValue) {
                                    in 1..3 -> q1 += amount
                                    in 4..6 -> q2 += amount
                                    in 7..9 -> q3 += amount
                                    in 10..12 -> q4 += amount
                                }
                            }
                        } catch (e: Exception) {
                            // Skip order if date format is unexpected
                        }
                    }
                    financialReports = FinancialReports(daily, weekly, monthly, yearly, q1, q2, q3, q4)
                }

                when (val result = adminRepository.getDashboard()) {
                    is Resource.Success -> {
                        val dashboard = result.data
                        // Override the inaccurate totalRevenue with our computed value
                        val updatedDashboard = dashboard?.copy(totalRevenue = computedRevenue)
                        
                        _state.value = _state.value.copy(
                            dashboardData = updatedDashboard,
                            topClients = topClients,
                            reports = financialReports,
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
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "An unexpected error occurred",
                    isLoading = false
                )
            }
        }
    }
}
