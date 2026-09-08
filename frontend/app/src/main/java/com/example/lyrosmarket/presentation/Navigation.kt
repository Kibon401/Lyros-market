package com.example.lyrosmarket.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lyrosmarket.presentation.login.LoginScreen
import com.example.lyrosmarket.presentation.register.RegisterScreen
import com.example.lyrosmarket.presentation.home.HomeScreen
import com.example.lyrosmarket.presentation.product_details.ProductDetailsScreen
import com.example.lyrosmarket.presentation.search.SearchScreen
import com.example.lyrosmarket.presentation.cart.CartScreen
import com.example.lyrosmarket.presentation.profile.ProfileScreen
import com.example.lyrosmarket.presentation.checkout.CheckoutScreen
import com.example.lyrosmarket.presentation.payment.PaymentSuccessScreen
import com.example.lyrosmarket.presentation.payment.PaymentFailedScreen
import com.example.lyrosmarket.presentation.orders.MyOrdersScreen
import com.example.lyrosmarket.presentation.orders.OrderDetailsScreen
import com.example.lyrosmarket.presentation.orders.ReviewProductScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    if (role.equals("ADMIN", ignoreCase = true)) {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else if (role.equals("DRIVER", ignoreCase = true)) {
                        navController.navigate(Screen.DeliveryDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId))
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.MyOrders.route)
                }
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId))
                }
            )
        }
        composable(Screen.Cart.route) {
            CartScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCheckout = {
                    navController.navigate(Screen.Checkout.route)
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToMap = {
                    navController.navigate(Screen.MapAddressPicker.route)
                }
            )
        }
        composable(Screen.ProductDetails.route) {
            ProductDetailsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                }
            )
        }
        composable(Screen.Checkout.route) { backStackEntry ->
            val checkoutViewModel = androidx.hilt.navigation.compose.hiltViewModel<com.example.lyrosmarket.presentation.checkout.CheckoutViewModel>()
            
            val lat = backStackEntry.savedStateHandle.get<Double>("lat")
            val lng = backStackEntry.savedStateHandle.get<Double>("lng")
            
            LaunchedEffect(lat, lng) {
                if (lat != null && lng != null) {
                    checkoutViewModel.onMapLocationSelected(lat, lng)
                    backStackEntry.savedStateHandle.remove<Double>("lat")
                    backStackEntry.savedStateHandle.remove<Double>("lng")
                }
            }

            CheckoutScreen(
                viewModel = checkoutViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPaymentComplete = {
                    navController.navigate(Screen.PaymentSuccess.route)
                },
                onPaymentFailed = {
                    navController.navigate(Screen.PaymentFailed.route)
                },
                onNavigateToMap = {
                    navController.navigate(Screen.MapAddressPicker.route)
                }
            )
        }
        composable(Screen.MapAddressPicker.route) {
            com.example.lyrosmarket.presentation.checkout.MapAddressPickerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLocationSelected = { lat, lng ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("lat", lat)
                    navController.previousBackStackEntry?.savedStateHandle?.set("lng", lng)
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.PaymentSuccess.route) {
            PaymentSuccessScreen(
                onTrackOrder = {
                    navController.navigate(Screen.MyOrders.route)
                },
                onBackToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.PaymentFailed.route) {
            PaymentFailedScreen(
                onTryAgain = {
                    navController.popBackStack()
                },
                onChangePaymentMethod = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.MyOrders.route) {
            MyOrdersScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOrderClick = { orderId ->
                    navController.navigate(Screen.OrderDetails.createRoute(orderId))
                }
            )
        }
        composable(Screen.OrderDetails.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailsScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.ReviewProduct.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull() ?: 0
            ReviewProductScreen(
                productId = productId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSubmitReview = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.DeliveryDashboard.route) {
            com.example.lyrosmarket.presentation.delivery.DeliveryDashboardScreen(
                onNavigateToOrderDetails = { orderId ->
                    navController.navigate(Screen.DeliveryOrderDetails.createRoute(orderId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.DeliveryOrderDetails.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            com.example.lyrosmarket.presentation.delivery.DeliveryOrderDetailsScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.AdminDashboard.route) {
            com.example.lyrosmarket.presentation.admin.dashboard.AdminDashboardScreen(
                onNavigateToProducts = { navController.navigate(Screen.AdminProducts.route) },
                onNavigateToCategories = { navController.navigate(Screen.AdminCategories.route) },
                onNavigateToUsers = { navController.navigate(Screen.AdminUsers.route) },
                onNavigateToOrders = { navController.navigate(Screen.AdminOrders.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AdminProducts.route) {
            com.example.lyrosmarket.presentation.admin.products.AdminProductsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdminCategories.route) {
            com.example.lyrosmarket.presentation.admin.categories.AdminCategoriesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdminUsers.route) {
            com.example.lyrosmarket.presentation.admin.users.AdminUsersScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdminOrders.route) {
            com.example.lyrosmarket.presentation.admin.orders.AdminOrdersScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
