package com.example.lyrosmarket.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.lyrosmarket.R
import com.example.lyrosmarket.domain.model.Category
import com.example.lyrosmarket.domain.model.Product
import com.example.lyrosmarket.ui.theme.Primary
import com.example.lyrosmarket.ui.theme.Secondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProduct: (Int) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToOrders: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Lyros Market", 
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        fontSize = 24.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Open Drawer */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Primary)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        BadgedBox(
                            badge = { 
                                if (state.cartBadgeCount > 0) {
                                    Badge(containerColor = Color(0xFFC05746)) { 
                                        Text(state.cartBadgeCount.toString()) 
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Outlined.ShoppingBasket, contentDescription = "Cart", tint = Primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            LyrosBottomNavigation(
                currentRoute = "home",
                onHomeClick = { /* Already here */ },
                onOrdersClick = onNavigateToOrders,
                onCartClick = onNavigateToCart,
                onProfileClick = onNavigateToProfile,
                cartBadgeCount = state.cartBadgeCount
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Secondary.copy(alpha = 0.1f), Color.White)))
        ) {
            // Search Bar
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onFilterClick = { /* TODO */ }
            )

            // Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(140.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home_banner),
                    contentDescription = "Farm Landscape",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Fresh from the Farm",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Get up to 30% off organic produce",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }

            // Categories
            CategorySection(
                categories = state.categories,
                selectedCategoryId = state.selectedCategoryId,
                onCategorySelected = { viewModel.onCategorySelected(it) }
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.error, color = MaterialTheme.colorScheme.error)
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalItemSpacing = 16.dp
                ) {
                    items(state.products) { product ->
                        ProductItem(
                            product = product, 
                            onClick = { onNavigateToProduct(product.id) },
                            onAddToCart = { viewModel.onAddToCart(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search fresh produce...", color = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        trailingIcon = { 
            IconButton(onClick = onFilterClick) {
                Icon(Icons.Default.Tune, contentDescription = "Filter", tint = Color.Gray)
            }
        },
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = 0.8f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.8f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun CategorySection(
    categories: List<Category>,
    selectedCategoryId: Int?,
    onCategorySelected: (Int?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        item {
            CategoryChip(
                name = "All",
                isSelected = selectedCategoryId == null,
                onClick = { onCategorySelected(null) }
            )
        }
        items(categories) { category ->
            CategoryChip(
                name = category.name,
                isSelected = selectedCategoryId == category.id,
                onClick = { onCategorySelected(category.id) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Primary else Color.White.copy(alpha = 0.8f),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Text(
            text = name,
            color = if (isSelected) Color.White else Color.DarkGray,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ProductItem(product: Product, onClick: () -> Unit, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Badge (Farm Direct / Organic)
                Surface(
                    modifier = Modifier.padding(12.dp),
                    color = if (product.id % 2 == 0) Color(0xFFC05746) else Secondary,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = if (product.id % 2 == 0) "Farm Direct" else "Organic",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Lyros Farms • 1 Bunch", 
                    color = Color.Gray, 
                    fontSize = 12.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "KES ${product.price}", 
                        color = Primary, 
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Primary,
                        shadowElevation = 4.dp
                    ) {
                        IconButton(onClick = onAddToCart, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

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
