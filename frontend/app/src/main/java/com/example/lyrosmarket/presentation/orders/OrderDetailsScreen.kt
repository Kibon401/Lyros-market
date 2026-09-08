package com.example.lyrosmarket.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import com.example.lyrosmarket.ui.theme.Primary
import com.example.lyrosmarket.ui.theme.Secondary
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.Brush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: String,
    onNavigateBack: () -> Unit,
    viewModel: OrderDetailsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    androidx.compose.runtime.LaunchedEffect(key1 = orderId) {
        viewModel.loadOrder(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details", fontWeight = FontWeight.Bold, color = Primary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Primary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Navigate to Cart */ }) {
                        Icon(Icons.Outlined.ShoppingBasket, contentDescription = "Cart", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(20.dp)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = { /* Help */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB4CDB7).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = Color(0xFF1B3D17))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Need Help with this Order?", color = Color(0xFF1B3D17), fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { /* Download Invoice */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Text(text = "Download Invoice", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Secondary.copy(alpha = 0.2f), Color.White)))
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.error, color = MaterialTheme.colorScheme.error)
                }
            } else if (state.order != null) {
                val order = state.order
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "ORDER #${order.id}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text(text = "Placed on ${order.date}", fontSize = 12.sp, color = Color.Gray)
                    }
                    Surface(color = Color(0xFFB4CDB7).copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp)) {
                        Text(text = order.status.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Delivery Status Card
                InfoCard(title = "Delivery Status") {
                    StatusTimelineItem(title = "Order Confirmed", subtitle = "The farm has accepted your order.", isCompleted = true, isCurrent = false)
                    StatusTimelineItem(
                        title = order.status, 
                        subtitle = "Status: ${order.status}", 
                        isCompleted = order.status != "Pending", 
                        isCurrent = true,
                        isLast = true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Delivery Address Card
                InfoCard(title = "Delivery Address", icon = Icons.Default.LocationOn) {
                    Text(text = "Delivery Point", fontWeight = FontWeight.Bold)
                    Text(text = order.shippingAddress, color = Color.Gray, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Payment Method Card
                InfoCard(title = "Payment Method", icon = Icons.Default.Payments) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF1B3D17).copy(alpha = 0.1f))) {
                            // M-Pesa icon placeholder
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "M-Pesa", fontWeight = FontWeight.Bold)
                            Text(text = "STK Push Payment", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Order Items
                Text(text = "Order Items (${order.items.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Primary)
                Spacer(modifier = Modifier.height(16.dp))
                order.items.forEach { item ->
                    OrderItemDetail(
                        name = item.productName, 
                        price = "KES ${item.price.toInt()}", 
                        quantity = item.quantity,
                        imageUrl = item.imageUrl
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Summary Card (Total)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3D17))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        SummaryRow(label = "Subtotal", value = "KES ${(order.totalAmount - order.shippingFee).toInt()}")
                        SummaryRow(label = "Delivery Fee", value = "KES ${order.shippingFee.toInt()}")
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.1f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Total", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "KES ${order.totalAmount.toInt()}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
}

@Composable
fun InfoCard(title: String, icon: ImageVector? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Primary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun StatusTimelineItem(title: String, subtitle: String, isCompleted: Boolean, isCurrent: Boolean, isLast: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) Primary else Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            if (!isLast) {
                Box(modifier = Modifier.width(2.dp).height(40.dp).background(if (isCompleted) Primary else Color.LightGray))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium, color = if (isCurrent) Color.Black else Color.Gray)
            Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun OrderItemDetail(name: String, price: String, quantity: Int, imageUrl: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(16.dp)).background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontWeight = FontWeight.Bold)
            Text(text = "Farm Fresh", fontSize = 12.sp, color = Color.Gray)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = price, fontWeight = FontWeight.Bold, color = Primary)
            Text(text = "Qty: $quantity", fontSize = 12.sp, color = Color.Gray)
        }
    }
    HorizontalDivider(color = Color(0xFFF5F5F5))
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = Color.White.copy(alpha = 0.7f))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun OrderDetailsPreview() {
    OrderDetailsScreen(orderId = "KJ-99283-ELD", onNavigateBack = {})
}
