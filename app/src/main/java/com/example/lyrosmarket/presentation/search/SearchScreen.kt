package com.example.lyrosmarket.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lyrosmarket.presentation.home.ProductItem
import com.example.lyrosmarket.presentation.home.CategorySection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.lyrosmarket.ui.theme.Secondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToProduct: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val searchQuery = viewModel.searchQuery.value

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    title = {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = viewModel::onSearchQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp),
                            placeholder = { Text("Search produce...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.extraLarge,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.8f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.8f),
                                disabledContainerColor = Color.White.copy(alpha = 0.8f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }
                )
                if (state.categories.isNotEmpty()) {
                    CategorySection(
                        categories = state.categories,
                        selectedCategoryId = state.selectedCategoryId,
                        onCategorySelected = { viewModel.onCategorySelected(it) }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Secondary.copy(alpha = 0.2f), Color.White)))
                .padding(padding)
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.error, color = MaterialTheme.colorScheme.error)
                }
            } else if (state.products.isEmpty() && (searchQuery.isNotEmpty() || state.selectedCategoryId != null)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No products found.")
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
