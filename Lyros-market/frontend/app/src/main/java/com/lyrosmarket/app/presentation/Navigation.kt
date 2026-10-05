package com.lyrosmarket.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lyrosmarket.app.presentation.components.LyrosBottomNavigation
import com.lyrosmarket.app.presentation.login.LoginScreen
import com.lyrosmarket.app.presentation.register.RegisterScreen
import com.lyrosmarket.app.presentation.home.HomeScreen
import com.lyrosmarket.app.presentation.product_details.ProductDetailsScreen
import com.lyrosmarket.app.presentation.search.SearchScreen
import com.lyrosmarket.app.presentation.cart.CartScreen
import com.lyrosmarket.app.presentation.profile.ProfileScreen
import com.lyrosmarket.app.presentation.checkout.CheckoutScreen
import com.lyrosmarket.app.presentation.payment.PaymentSuccessScreen
import com.lyrosmarket.app.presentation.payment.PaymentFailedScreen
import com.lyrosmarket.app.presentation.orders.MyOrdersScreen
import com.lyrosmarket.app.presentation.orders.OrderDetailsScreen
import com.lyrosmarket.app.presentation.orders.ReviewProductScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""
    val mainViewModel = hiltViewModel<MainViewModel>()
    val cartBadgeCount by mainViewModel.cartBadgeCount.collectAsState()

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Cart.route,
        Screen.Profile.route,
        Screen.MyOrders.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                LyrosBottomNavigation(
                    currentRoute = currentRoute,
                    onHomeClick = { navController.navigate(Screen.Home.route) { launchSingleTop = true; popUpTo(Screen.Home.route) } },
                    onOrdersClick = { navController.navigate(Screen.MyOrders.route) { launchSingleTop = true } },
                    onCartClick = { navController.navigate(Screen.Cart.route) { launchSingleTop = true } },
                    onProfileClick = { navController.navigate(Screen.Profile.route) { launchSingleTop = true } },
                    cartBadgeCount = cartBadgeCount
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = mainViewModel.startDestination,
            modifier = androidx.compose.ui.Modifier.padding(paddingValues)
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
        composable(Screen.Cart.route) { backStackEntry ->
            val cartViewModel = androidx.hilt.navigation.compose.hiltViewModel<com.lyrosmarket.app.presentation.cart.CartViewModel>()
            val lat = backStackEntry.savedStateHandle.get<Double>("lat")
            val lng = backStackEntry.savedStateHandle.get<Double>("lng")
            
            LaunchedEffect(lat, lng) {
                if (lat != null && lng != null) {
                    cartViewModel.onMapLocationSelected(lat, lng)
                    backStackEntry.savedStateHandle.remove<Double>("lat")
                    backStackEntry.savedStateHandle.remove<Double>("lng")
                }
            }
            CartScreen(
                viewModel = cartViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCheckout = {
                    navController.navigate(Screen.Checkout.route)
                },
                onNavigateToMap = {
                    navController.navigate(Screen.MapAddressPicker.route)
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
                },
                onNavigateToPasswordReset = {
                    navController.navigate(Screen.PasswordReset.route)
                }
            )
        }
        composable(Screen.PasswordReset.route) {
            com.lyrosmarket.app.presentation.profile.PasswordResetScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResetSuccess = {
                    navController.popBackStack()
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
                },
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetails.createRoute(productId))
                }
            )
        }
        composable(Screen.Checkout.route) { backStackEntry ->
            val checkoutViewModel = androidx.hilt.navigation.compose.hiltViewModel<com.lyrosmarket.app.presentation.checkout.CheckoutViewModel>()
            
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
            val savedLocation = mainViewModel.getSavedLocation()
            com.lyrosmarket.app.presentation.checkout.MapAddressPickerScreen(
                initialLocation = savedLocation,
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
            com.lyrosmarket.app.presentation.delivery.DeliveryDashboardScreen(
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
            com.lyrosmarket.app.presentation.delivery.DeliveryOrderDetailsScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.AdminDashboard.route) {
            com.lyrosmarket.app.presentation.admin.dashboard.AdminDashboardScreen(
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
            com.lyrosmarket.app.presentation.admin.products.AdminProductsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdminCategories.route) {
            com.lyrosmarket.app.presentation.admin.categories.AdminCategoriesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdminUsers.route) {
            com.lyrosmarket.app.presentation.admin.users.AdminUsersScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AdminOrders.route) {
            com.lyrosmarket.app.presentation.admin.orders.AdminOrdersScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        }
    }
}
