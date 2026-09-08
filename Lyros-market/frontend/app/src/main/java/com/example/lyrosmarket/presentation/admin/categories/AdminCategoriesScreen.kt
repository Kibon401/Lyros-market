package com.example.lyrosmarket.presentation.admin.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lyrosmarket.ui.theme.Primary
import com.example.lyrosmarket.ui.theme.Secondary
import com.example.lyrosmarket.ui.theme.Tertiary
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoriesScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminCategoriesViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<com.example.lyrosmarket.data.remote.dto.CategoryDto?>(null) }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
            if (message.contains("successfully")) {
                showAddDialog = false
                categoryToEdit = null
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Manage Categories", color = Primary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    categoryToEdit = null
                    showAddDialog = true 
                },
                containerColor = Primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Secondary.copy(alpha = 0.2f), Color.White)))
        ) {
            if (state.isLoading && state.categories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (state.categories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No categories found.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.categories) { category ->
                    AdminCategoryCard(
                        category = category,
                        onEditClick = {
                            categoryToEdit = category
                            showAddDialog = true
                        },
                        onDeleteClick = { viewModel.deleteCategory(category.id) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddCategoryDialog(
                categoryToEdit = categoryToEdit,
                onDismiss = { 
                    showAddDialog = false
                    categoryToEdit = null 
                },
                onAddCategory = { name, desc, pId ->
                    viewModel.createCategory(name, desc, pId)
                },
                onUpdateCategory = { id, name, desc, pId ->
                    viewModel.updateCategory(id, name, desc, pId)
                },
                isLoading = state.isLoading
            )
        }
    }
}
}

@Composable
fun AdminCategoryCard(
    category: com.example.lyrosmarket.data.remote.dto.CategoryDto,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = category.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = category.description, fontSize = 14.sp, color = Color.Gray)
                if (category.parentId != null) {
                    Text(text = "Subcategory of ID: ${category.parentId}", fontSize = 12.sp, color = Primary)
                }
            }
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Secondary)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Tertiary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(
    categoryToEdit: com.example.lyrosmarket.data.remote.dto.CategoryDto?,
    onDismiss: () -> Unit,
    onAddCategory: (String, String, Int?) -> Unit,
    onUpdateCategory: (Int, String, String, Int?) -> Unit,
    isLoading: Boolean
) {
    var name by remember { mutableStateOf(categoryToEdit?.name ?: "") }
    var description by remember { mutableStateOf(categoryToEdit?.description ?: "") }
    var parentId by remember { mutableStateOf(categoryToEdit?.parentId?.toString() ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(if (categoryToEdit == null) "Create a New Category" else "Edit Category", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Category Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = parentId,
                onValueChange = { parentId = it },
                label = { Text("Parent Category ID (Optional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val pId = parentId.toIntOrNull()
                    if (categoryToEdit == null) {
                        onAddCategory(name, description, pId)
                    } else {
                        onUpdateCategory(categoryToEdit.id, name, description, pId)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (categoryToEdit == null) "Create Category" else "Update Category", fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
