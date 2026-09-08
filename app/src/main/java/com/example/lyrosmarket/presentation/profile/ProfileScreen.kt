package com.example.lyrosmarket.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.lyrosmarket.ui.theme.Primary
import com.example.lyrosmarket.ui.theme.Secondary
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToMap: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val fullName by viewModel.fullName
    val email by viewModel.email

    LaunchedEffect(key1 = true) {
        viewModel.logoutEvent.collectLatest {
            onLogout()
        }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Avatar Section
            Box(contentAlignment = Alignment.BottomEnd) {
                val initials = remember(fullName) {
                    if (fullName.isNotBlank()) {
                        val parts = fullName.trim().split("\\s+".toRegex())
                        if (parts.isNotEmpty()) {
                            if (parts.size == 1) {
                                parts[0].take(2).uppercase()
                            } else {
                                (parts[0].take(1) + parts[1].take(1)).uppercase()
                            }
                        } else "KM"
                    } else "KM"
                }

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Icon(
                        Icons.Default.Edit, 
                        contentDescription = null, 
                        modifier = Modifier.padding(6.dp).size(16.dp),
                        tint = Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = fullName.ifBlank { "User" }, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = "Member since 2026", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(32.dp))

            // Input Fields
            ProfileSection(title = "Personal Information") {
                ProfileTextField(label = "Full Name", value = fullName, onValueChange = viewModel::onFullNameChange)
                Spacer(modifier = Modifier.height(16.dp))
                ProfileTextField(label = "Email Address", value = email, onValueChange = viewModel::onEmailChange)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Security Section
            ProfileSection(title = "Security & Access") {
                ProfileOptionButton(label = "Change Password", icon = Icons.Default.ChevronRight, onClick = { /* TODO */ })
                Spacer(modifier = Modifier.height(12.dp))
                ProfileOptionButton(label = "Manage Addresses", icon = Icons.Default.LocationOn, onClick = onNavigateToMap)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Action Buttons
            Button(
                onClick = { /* TODO: Implement save changes if needed */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D17)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(text = "Save Changes", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = viewModel::logout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDEDD)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFB3261E), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Logout", color = Color(0xFFB3261E), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { /* TODO: Delete Account */ }) {
                Text(text = "Delete Account", color = Color.Gray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title, 
            fontWeight = FontWeight.Bold, 
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
            elevation = CardDefaults.cardElevation(0.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ProfileTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF3F3F3),
                unfocusedContainerColor = Color(0xFFF3F3F3),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@Composable
fun ProfileOptionButton(label: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF3F3F3)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontWeight = FontWeight.Medium)
            Icon(icon, contentDescription = null, tint = Color.Gray)
        }
    }
}
