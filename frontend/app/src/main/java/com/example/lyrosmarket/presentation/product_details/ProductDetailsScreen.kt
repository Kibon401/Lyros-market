package com.example.lyrosmarket.presentation.product_details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.lyrosmarket.domain.model.Product
import com.example.lyrosmarket.ui.theme.Primary
import com.example.lyrosmarket.ui.theme.Secondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    var quantity by remember { mutableStateOf(1) }

    state.product?.let { product ->
        ProductDetailsContent(
            product = product,
            quantity = quantity,
            onQuantityChange = { quantity = it },
            onNavigateBack = onNavigateBack,
            onNavigateToCart = onNavigateToCart,
            onAddToCart = { 
                viewModel.onAddToCart(quantity)
                onNavigateToCart()
            }
        )
    } ?: run {
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.error, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsContent(
    product: Product,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onAddToCart: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            ProductBottomBar(
                price = product.price,
                quantity = quantity,
                onQuantityChange = onQuantityChange,
                onAddToCart = onAddToCart
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Secondary.copy(alpha = 0.2f), Color.White)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
            // Header Image with Badges and Overlays
            Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Top Navigation Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.8f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    IconButton(
                        onClick = onNavigateToCart,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.8f), CircleShape)
                    ) {
                        BadgedBox(badge = { Badge { Text("1") } }) {
                            Icon(Icons.Outlined.ShoppingBasket, contentDescription = "Cart")
                        }
                    }
                }

                // Badges at the bottom left of image
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BadgeChip(text = "Organic Certified", containerColor = Color(0xFFB4CDB7), textColor = Primary)
                    BadgeChip(text = "Direct from Farm", containerColor = Color(0xFF7D2E24), textColor = Color.White)
                }
            }

            // Main Product Info Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White.copy(alpha = 0.9f)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.name,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Outlined.FavoriteBorder, 
                            contentDescription = "Favorite",
                            tint = Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { 
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(18.dp)) 
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "4.8 (124 reviews)", color = Color.Gray, fontSize = 14.sp)
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "KSh ${product.price.toInt()}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Primary
                        )
                        Text(
                            text = " per kg",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), color = Color(0xFFEEEEEE))

                    // About this item
                    var isExpanded by remember { mutableStateOf(false) }
                    Text(text = "About this item", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = product.description.ifEmpty { "Grown in the rich, volcanic soils of Kiambu, these heritage tomatoes are prized for their intense, complex flavor..." },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isExpanded) "Read less" else "Read more",
                        color = Primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable { isExpanded = !isExpanded }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Product Details Section
                    Text(text = "Product Details", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailRow(label = "Category", value = "Fresh Produce - Vegetables")
                    DetailRow(label = "Farming Method", value = "100% Organic, No Pesticides")
                    DetailRow(label = "Storage", value = "Room temperature, away from direct sun")
                    DetailRow(label = "Shelf Life", value = "5-7 days after delivery")

                    Spacer(modifier = Modifier.height(32.dp))

                    // Reviews Section Placeholder
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Verified Purchase Reviews", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    
                    ReviewItem(name = "Wanjiku K.", comment = "Absolutely delicious! You can really taste the difference compared to supermarket tomatoes...")
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    // You might also like
                    Text(text = "You might also like", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    RelatedProductsList()
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
}

@Composable
fun BadgeChip(text: String, containerColor: Color, textColor: Color) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (textColor == Primary) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, modifier = Modifier.weight(1f))
        Text(text = value, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.5f))
    }
    HorizontalDivider(color = Color(0xFFF5F5F5))
}

@Composable
fun ReviewItem(name: String, comment: String) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Primary)) {
                Text(text = name.take(1), color = Color.White, modifier = Modifier.align(Alignment.Center))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = name, fontWeight = FontWeight.Bold)
                Row {
                    repeat(5) { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(14.dp)) }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "7 days ago", color = Color.Gray, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = comment, color = Color.DarkGray, fontSize = 14.sp, lineHeight = 20.sp)
    }
}

@Composable
fun RelatedProductsList() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(3) {
            Card(
                modifier = Modifier.width(160.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    AsyncImage(
                        model = "",
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Product Name", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(text = "KSh 200 / kg", color = Primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductBottomBar(
    price: Double,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Total Price", color = Color.Gray, fontSize = 12.sp)
                Text(
                    text = "KSh ${(price * quantity).toInt()}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Primary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Quantity Selector
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFF3F3F3)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        Text(text = quantity.toString(), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                        IconButton(onClick = { onQuantityChange(quantity + 1) }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D17)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailsPreview() {
    val mockProduct = Product(1, "Organic Heritage Tomatoes", "Grown in the rich, volcanic soils of Kiambu, these heritage tomatoes are prized for their intense, complex flavor...", 350.0, 1, "", 10, false)
    ProductDetailsContent(
        product = mockProduct,
        quantity = 1,
        onQuantityChange = {},
        onNavigateBack = {},
        onNavigateToCart = {},
        onAddToCart = {}
    )
}
