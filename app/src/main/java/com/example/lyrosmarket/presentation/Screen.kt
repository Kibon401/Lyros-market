package com.example.lyrosmarket.presentation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Search : Screen("search")
    object ProductDetails : Screen("product_details/{productId}") {
        fun createRoute(productId: Int) = "product_details/$productId"
    }
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object Checkout : Screen("checkout")
    object PaymentSuccess : Screen("payment_success")
    object PaymentFailed : Screen("payment_failed")
    object MyOrders : Screen("my_orders")
    object OrderDetails : Screen("order_details/{orderId}") {
        fun createRoute(orderId: String) = "order_details/$orderId"
    }
    object ReviewProduct : Screen("review_product/{productId}") {
        fun createRoute(productId: Int) = "review_product/$productId"
    }
    object MapAddressPicker : Screen("map_address_picker")
    object DeliveryDashboard : Screen("delivery_dashboard")
    object DeliveryOrderDetails : Screen("delivery_order_details/{orderId}") {
        fun createRoute(orderId: String) = "delivery_order_details/$orderId"
    }
    object AdminDashboard : Screen("admin_dashboard")
    object AdminProducts : Screen("admin_products")
    object AdminCategories : Screen("admin_categories")
    object AdminUsers : Screen("admin_users")
    object AdminOrders : Screen("admin_orders")
}
