package com.example.lyrosmarket.presentation.admin.products

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.lyrosmarket.ui.theme.Primary
import com.example.lyrosmarket.ui.theme.Secondary
import com.example.lyrosmarket.ui.theme.Tertiary
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminProductsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<com.example.lyrosmarket.data.remote.dto.ProductDto?>(null) }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
            if (message.contains("successfully")) {
                showAddDialog = false
                productToEdit = null
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Manage Inventory", color = Primary, fontWeight = FontWeight.Bold) },
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
                    productToEdit = null
                    showAddDialog = true 
                },
                containerColor = Primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Secondary.copy(alpha = 0.2f), Color.White)))
        ) {
            if (state.isLoading && state.products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (state.products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No products in inventory.", color = Color.Gray)
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
                items(state.products) { product ->
                    AdminProductCard(
                        product = product,
                        onEditClick = { 
                            productToEdit = product
                            showAddDialog = true
                        },
                        onDeleteClick = { viewModel.deleteProduct(product.id) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddProductDialog(
                productToEdit = productToEdit,
                onDismiss = { 
                    showAddDialog = false
                    productToEdit = null 
                },
                onAddProduct = { name, desc, price, catId, stock, isHighDemand, imageFile ->
                    viewModel.addProduct(name, desc, price, catId, stock, isHighDemand, imageFile)
                },
                onUpdateProduct = { id, name, desc, price, catId, stock, isHighDemand, imageFile, oldImageUrl ->
                    viewModel.updateProduct(id, name, desc, price, catId, stock, isHighDemand, imageFile, oldImageUrl)
                },
                isUploadingImage = state.isUploadingImage,
                isLoading = state.isLoading
            )
        }
    }
}
}

@Composable
fun AdminProductCard(
    product: com.example.lyrosmarket.data.remote.dto.ProductDto,
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
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "KSh ${product.price}", fontSize = 14.sp, color = Primary, fontWeight = FontWeight.SemiBold)
                Text(text = "Stock: ${product.stockQuantity}", fontSize = 12.sp, color = if (product.stockQuantity < 10) Color.Red else Color.Gray)
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
fun AddProductDialog(
    productToEdit: com.example.lyrosmarket.data.remote.dto.ProductDto?,
    onDismiss: () -> Unit,
    onAddProduct: (String, String, Double, Int, Int, Boolean, File?) -> Unit,
    onUpdateProduct: (Int, String, String, Double, Int, Int, Boolean, File?, String) -> Unit,
    isUploadingImage: Boolean,
    isLoading: Boolean
) {
    var name by remember { mutableStateOf(productToEdit?.name ?: "") }
    var description by remember { mutableStateOf(productToEdit?.description ?: "") }
    var price by remember { mutableStateOf(productToEdit?.price?.toString() ?: "") }
    var categoryId by remember { mutableStateOf(productToEdit?.categoryId?.toString() ?: "") }
    var stock by remember { mutableStateOf(productToEdit?.stockQuantity?.toString() ?: "") }
    var isHighDemand by remember { mutableStateOf(productToEdit?.isHighDemand ?: false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(if (productToEdit == null) "Add New Product" else "Edit Product", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
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
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock Qty") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = categoryId,
                onValueChange = { categoryId = it },
                label = { Text("Category ID") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isHighDemand,
                    onCheckedChange = { isHighDemand = it },
                    colors = CheckboxDefaults.colors(checkedColor = Primary)
                )
                Text("Is High Demand?")
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text(if (selectedImageUri == null) "Select New Image" else "New Image Selected", color = Color.Black)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    val p = price.toDoubleOrNull() ?: 0.0
                    val c = categoryId.toIntOrNull() ?: 1
                    val s = stock.toIntOrNull() ?: 0
                    
                    var file: File? = null
                    selectedImageUri?.let { uri ->
                        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                        file = File(context.cacheDir, "upload_image.jpg")
                        val outputStream = FileOutputStream(file)
                        inputStream?.copyTo(outputStream)
                    }
                    
                    if (productToEdit == null) {
                        onAddProduct(name, description, p, c, s, isHighDemand, file)
                    } else {
                        onUpdateProduct(productToEdit.id, name, description, p, c, s, isHighDemand, file, productToEdit.imageUrl)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = !isLoading
            ) {
                if (isUploadingImage) {
                    Text("Uploading Image...")
                } else if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (productToEdit == null) "Create Product" else "Update Product")
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
