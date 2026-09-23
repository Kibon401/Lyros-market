package com.lyrosmarket.app.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LyrosBottomNavigation(
    currentRoute: String,
    onHomeClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    cartBadgeCount: Int = 0
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant, // Light grayish/greenish bottom bar
        tonalElevation = 8.dp
    ) {
        BottomNavItem(
            selected = currentRoute == "home",
            onClick = onHomeClick,
            icon = Icons.Filled.Home,
            label = "Home"
        )
        BottomNavItem(
            selected = currentRoute == "orders",
            onClick = onOrdersClick,
            icon = Icons.Default.ListAlt,
            label = "Orders"
        )
        BottomNavItem(
            selected = currentRoute == "cart",
            onClick = onCartClick,
            icon = Icons.Outlined.ShoppingCart,
            label = "Cart",
            badgeCount = if (cartBadgeCount > 0) cartBadgeCount else null
        )
        BottomNavItem(
            selected = currentRoute == "profile",
            onClick = onProfileClick,
            icon = Icons.Outlined.Person,
            label = "Profile"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.BottomNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    badgeCount: Int? = null
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            if (badgeCount != null) {
                BadgedBox(
                    badge = { Badge(containerColor = MaterialTheme.colorScheme.tertiary) { Text(badgeCount.toString(), color = MaterialTheme.colorScheme.onTertiary) } }
                ) {
                    Icon(icon, contentDescription = label)
                }
            } else {
                Icon(icon, contentDescription = label)
            }
        },
        label = { Text(label, fontSize = 10.sp) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
