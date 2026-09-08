package com.example.lyrosmarket.presentation.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lyrosmarket.ui.theme.Primary
import com.example.lyrosmarket.ui.theme.Secondary
import androidx.compose.ui.graphics.Brush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onPaymentComplete: () -> Unit,
    onPaymentFailed: () -> Unit,
    onNavigateToMap: () -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(state.isPaymentInitiated) {
        if (state.isPaymentInitiated) {
            onPaymentComplete()
        }
    }

    LaunchedEffect(state.isPaymentFailed) {
        if (state.isPaymentFailed) {
            onPaymentFailed()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold, color = Primary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(24.dp)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = { viewModel.initiatePayment(state.selectedAddress?.fullAddress ?: "Eldoret CBD") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D17)),
                    shape = RoundedCornerShape(32.dp),
                    enabled = !state.isLoading
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = "Pay KSh ${state.total.toInt()}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                        }
                    }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Order Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Order Summary",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B3D17)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${state.itemCount} Items • Farm-to-Door Delivery",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "KSh ${state.total.toInt()}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B3D17)
                            )
                            Text(
                                text = "TOTAL (Incl. Shipping)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF5F5F5))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF1B3D17),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = state.selectedAddress?.label ?: "Select Address", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = state.selectedAddress?.fullAddress ?: "No address selected", color = Color.Gray, fontSize = 14.sp)
                        }
                        TextButton(onClick = onNavigateToMap) {
                            Text("CHANGE", color = Primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // M-Pesa Payment Section
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = Color(0xFF1B3D17))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "M-Pesa Payment",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B3D17)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Enter your M-Pesa number below. An STK push will be sent to your phone for PIN entry to complete the transaction.",
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "PHONE NUMBER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B3D17)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.phoneNumber,
                    onValueChange = { viewModel.onPhoneNumberChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    prefix = { Text("+254 ", fontWeight = FontWeight.SemiBold) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF1F1EB),
                        unfocusedContainerColor = Color(0xFFF1F1EB),
                        focusedIndicatorColor = Color(0xFFB4CDB7),
                        unfocusedIndicatorColor = Color(0xFFB4CDB7)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    enabled = !state.isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ensure your phone is unlocked and nearby.",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutPreview() {
    CheckoutScreen(onNavigateBack = {}, onPaymentComplete = {}, onPaymentFailed = {}, onNavigateToMap = {})
}
