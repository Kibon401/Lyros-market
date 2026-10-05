package com.lyrosmarket.app.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.lyrosmarket.app.ui.theme.Primary
import com.lyrosmarket.app.ui.theme.Secondary
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll

import androidx.compose.material3.pulltorefresh.PullToRefreshBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    onNavigateBack: () -> Unit,
    onOrderClick: (String) -> Unit,
    viewModel: MyOrdersViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    var selectedFilter by remember { mutableStateOf("All Orders") }
    val filters = listOf("All Orders", "Delivered", "In Transit")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lyros Market", fontWeight = FontWeight.Bold, color = Primary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Primary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Navigate to Cart */ }) {
                        Icon(Icons.Outlined.ShoppingBasket, contentDescription = "Cart", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        var isRefreshing by remember { mutableStateOf(false) }

        LaunchedEffect(state.isLoading) {
            if (!state.isLoading) {
                isRefreshing = false
            }
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.loadOrders()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Secondary.copy(alpha = 0.2f), Color.White)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "My Orders", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B3D17))
                Text(text = "Track your farm-fresh deliveries.", color = Color.Gray, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(24.dp))

                // Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFB4CDB7),
                                selectedLabelColor = Primary,
                                containerColor = Color(0xFFE5E2D9).copy(alpha = 0.5f),
                                labelColor = Color.Gray
                            ),
                            border = null,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Filter Orders
                val filteredOrders = remember(state.orders, selectedFilter) {
                    when (selectedFilter) {
                        "All Orders" -> state.orders
                        "Delivered" -> state.orders.filter { it.status.equals("DELIVERED", ignoreCase = true) }
                        "In Transit" -> state.orders.filter { 
                            val s = it.status.uppercase()
                            s == "ACCEPTED" || s == "PICKED_UP" || s == "IN_TRANSIT"
                        }
                        else -> state.orders
                    }
                }

                // Orders List
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                } else if (state.error != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.error, color = MaterialTheme.colorScheme.error)
                    }
                } else if (filteredOrders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No orders found.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(filteredOrders) { order ->
                            val imageUrl = order.items.firstOrNull()?.imageUrl ?: ""
                            OrderItemCard(
                                orderId = order.id,
                                date = order.date,
                                itemCount = order.items.size,
                                amount = order.totalAmount.toString(),
                                status = order.status,
                                imageUrl = imageUrl,
                                onClick = { onOrderClick(order.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(
    orderId: String,
    date: String,
    itemCount: Int,
    amount: String,
    status: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    val formattedDate = remember(date) {
        if (date.contains("T")) {
            val parts = date.split("T")
            val datePart = parts.getOrNull(0) ?: ""
            val timePart = parts.getOrNull(1)?.take(5) ?: ""
            if (datePart.isNotBlank() && timePart.isNotBlank()) "$datePart $timePart" else date
        } else {
            date
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            if (imageUrl.isNotBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE5E2D9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingBasket,
                        contentDescription = "Order",
                        tint = Primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = "Order #$orderId", 
                            fontSize = 20.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color(0xFF1B3D17),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$formattedDate • $itemCount items", 
                            color = Color.Gray, 
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(text = "KSh $amount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B3D17))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (status == "Delivered") Icons.Default.CheckCircle else Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = if (status == "Delivered") Color(0xFF1B3D17) else Color(0xFF7D2E24),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = status,
                        fontWeight = FontWeight.Bold,
                        color = if (status == "Delivered") Color(0xFF1B3D17) else Color(0xFF7D2E24),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyOrdersPreview() {
    MyOrdersScreen(onNavigateBack = {}, onOrderClick = {})
}
