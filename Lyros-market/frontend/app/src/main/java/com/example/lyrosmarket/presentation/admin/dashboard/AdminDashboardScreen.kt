package com.example.lyrosmarket.presentation.admin.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lyrosmarket.ui.theme.Primary
import com.example.lyrosmarket.ui.theme.Secondary
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToProducts: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

        Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard", fontWeight = FontWeight.Bold, color = Primary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Primary)
                    }
                }
            )
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.error, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadDashboard() }, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                        Text("Retry")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = "Key Performance Indicators",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // KPI Grid
                val kpis = listOf(
                    KpiItem("Revenue", "KSh ${state.dashboardData?.totalRevenue ?: 0.0}", Icons.Default.AttachMoney, MaterialTheme.colorScheme.primary),
                    KpiItem("Total Orders", "${state.dashboardData?.totalOrders ?: 0}", Icons.Default.ShoppingCart, MaterialTheme.colorScheme.secondary),
                    KpiItem("Active Users", "${state.dashboardData?.totalUsers ?: 0}", Icons.Default.People, MaterialTheme.colorScheme.tertiary),
                    KpiItem("Low Stock", "${state.dashboardData?.lowStockItems ?: 0}", Icons.Default.Warning, MaterialTheme.colorScheme.error)
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.weight(1f)) { KpiCard(kpis[0]) }
                        Box(modifier = Modifier.weight(1f)) { KpiCard(kpis[1]) }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.weight(1f)) { KpiCard(kpis[2]) }
                        Box(modifier = Modifier.weight(1f)) { KpiCard(kpis[3]) }
                    }
                }

                // Financial Reports
                Text(
                    text = "Financial Reports (Revenue)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp, top = 24.dp)
                )
                Text(
                    text = "Yearly includes all months in the current calendar year. Monthly is the current month only.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val periodData = mapOf(
                    "Daily" to state.reports.daily,
                    "Weekly" to state.reports.weekly,
                    "Monthly" to state.reports.monthly,
                    "Yearly" to state.reports.yearly
                )
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth().height(250.dp).padding(bottom = 16.dp)
                ) {
                    Box(modifier = Modifier.padding(16.dp).fillMaxSize(), contentAlignment = Alignment.Center) {
                        SimpleBarChart(
                            data = periodData,
                            modifier = Modifier.fillMaxSize(),
                            barColor = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Text(
                    text = "Quarterly Revenue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
                )
                
                val quarterData = mapOf(
                    "Q1" to state.reports.q1,
                    "Q2" to state.reports.q2,
                    "Q3" to state.reports.q3,
                    "Q4" to state.reports.q4
                )
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth().height(200.dp).padding(bottom = 16.dp)
                ) {
                    Box(modifier = Modifier.padding(16.dp).fillMaxSize(), contentAlignment = Alignment.Center) {
                        SimpleBarChart(
                            data = quarterData,
                            modifier = Modifier.fillMaxSize(),
                            barColor = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                // Top 5 Products
                if (!state.dashboardData?.topProducts.isNullOrEmpty()) {
                    Text(
                        text = "Top 5 Selling Products",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp, top = 24.dp)
                    )
                    state.dashboardData?.topProducts?.forEach { product ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = product.productName, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Text(text = "${product.totalSold} units sold", color = Color.Gray, fontSize = 12.sp)
                                }
                                Text(text = "KSh ${product.revenue}", color = Primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Top 5 Clients
                if (state.topClients.isNotEmpty()) {
                    Text(
                        text = "Top 5 Clients",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp, top = 24.dp)
                    )
                    state.topClients.forEachIndexed { index, client ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(androidx.compose.foundation.shape.CircleShape)
                                            .background(Primary.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${index + 1}", fontWeight = FontWeight.Bold, color = Primary)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = client.username, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Text(text = "KSh ${client.totalSpent}", color = Primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Management Modules",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Quick Actions
                ActionCard(
                    title = "Manage Products & Inventory",
                    subtitle = "Add, edit, or remove products and update stock.",
                    icon = Icons.Default.Inventory,
                    gradient = Brush.linearGradient(listOf(Color(0xFF2193b0), Color(0xFF6dd5ed))),
                    onClick = onNavigateToProducts
                )
                Spacer(modifier = Modifier.height(12.dp))
                ActionCard(
                    title = "Manage Categories",
                    subtitle = "Organize store categories and sub-categories.",
                    icon = Icons.Default.Category,
                    gradient = Brush.linearGradient(listOf(Color(0xFFcc2b5e), Color(0xFF753a88))),
                    onClick = onNavigateToCategories
                )
                Spacer(modifier = Modifier.height(12.dp))
                ActionCard(
                    title = "Manage Users & Roles",
                    subtitle = "View users, promote drivers, or assign admin roles.",
                    icon = Icons.Default.ManageAccounts,
                    gradient = Brush.linearGradient(listOf(Color(0xFFee0979), Color(0xFFff6a00))),
                    onClick = onNavigateToUsers
                )
                Spacer(modifier = Modifier.height(12.dp))
                ActionCard(
                    title = "View Platform Orders",
                    subtitle = "Track all orders across the entire Lyros Market.",
                    icon = Icons.Default.ReceiptLong,
                    gradient = Brush.linearGradient(listOf(Color(0xFF11998e), Color(0xFF38ef7d))),
                    onClick = onNavigateToOrders
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
}

data class KpiItem(val title: String, val value: String, val icon: ImageVector, val color: Color)

@Composable
fun KpiCard(item: KpiItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(item.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = item.value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.title, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: Brush,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100), label = "scale"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = subtitle, fontSize = 13.sp, color = Color.White.copy(alpha = 0.9f))
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun SimpleBarChart(
    data: Map<String, Double>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    if (data.isEmpty()) return
    
    val maxValue = data.values.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0
    
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val barWidth = width / (data.size * 2f)
        val spacing = barWidth
        
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.DKGRAY
            textSize = 24f // Reduced text size
            textAlign = android.graphics.Paint.Align.CENTER
        }
        
        val valuePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 20f // Reduced text size
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
        }

        data.entries.forEachIndexed { index, entry ->
            val (label, value) = entry
            val barHeight = (value / maxValue).toFloat() * (height - 60f)
            val startX = (index * (barWidth + spacing)) + spacing
            
            // Draw Bar
            drawRect(
                color = barColor,
                topLeft = Offset(startX, height - 30f - barHeight),
                size = Size(barWidth, barHeight)
            )
            
            // Draw Label (bottom axis)
            drawContext.canvas.nativeCanvas.drawText(
                label,
                startX + (barWidth / 2),
                height - 5f,
                textPaint
            )
            
            // Draw Value (above bar)
            val displayValue = if(value >= 1000) "${(value/1000).toInt()}k" else value.toInt().toString()
            drawContext.canvas.nativeCanvas.drawText(
                displayValue,
                startX + (barWidth / 2),
                height - 40f - barHeight,
                valuePaint
            )
        }
    }
}
