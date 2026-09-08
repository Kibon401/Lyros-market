package com.example.lyrosmarket.presentation.orders

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lyrosmarket.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewProductScreen(
    productId: Int,
    onNavigateBack: () -> Unit,
    onSubmitReview: () -> Unit
) {
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    val tags = listOf("Super Fresh", "Perfect Ripeness", "Eco-Packaging", "Great Value")
    val selectedTags = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lyros Market", fontWeight = FontWeight.Bold, color = Primary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9F7F2)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Product Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(16.dp)).background(Color.LightGray))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "DELIVERED YESTERDAY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text(text = "Organic Curly Kale", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B3D17))
                        Text(text = "From Green Earth Farms", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(text = "HOW WAS THE QUALITY?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(16.dp))

            // Star Rating
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(5) { index ->
                    val isSelected = index < rating
                    IconButton(onClick = { rating = index + 1 }, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (isSelected) Color(0xFFFFB400) else Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Text Review
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "SHARE YOUR EXPERIENCE", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    placeholder = { Text("Was the produce fresh? How was the packaging?", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color(0xFFE5E2D9),
                        unfocusedIndicatorColor = Color(0xFFE5E2D9)
                    )
                )
                Text(text = "OPTIONAL", modifier = Modifier.fillMaxWidth().padding(top = 4.dp, end = 16.dp), textAlign = TextAlign.End, fontSize = 10.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Tags
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "QUICK TAGS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    tags.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = { if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag) },
                            label = { Text(tag) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFF1F1EB),
                                selectedLabelColor = Primary,
                                containerColor = Color.White,
                                labelColor = Color.DarkGray
                            ),
                            border = FilterChipDefaults.filterChipBorder(borderColor = Color(0xFFE5E2D9), enabled = true, selected = isSelected)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Submit Button
            Button(
                onClick = onSubmitReview,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D17)),
                shape = RoundedCornerShape(32.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Submit Review", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your feedback helps our farmers improve and the Lyros community grow.",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = { content() }
    )
}

@Preview(showBackground = true)
@Composable
fun ReviewProductPreview() {
    ReviewProductScreen(productId = 1, onNavigateBack = {}, onSubmitReview = {})
}
