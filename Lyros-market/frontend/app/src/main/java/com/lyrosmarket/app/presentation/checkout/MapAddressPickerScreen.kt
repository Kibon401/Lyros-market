package com.lyrosmarket.app.presentation.checkout

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.location.LocationServices
import com.lyrosmarket.app.ui.theme.Primary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapAddressPickerScreen(
    initialLocation: Pair<Double, Double>? = null,
    onNavigateBack: () -> Unit,
    onLocationSelected: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    
    // Default location (Eldoret CBD or saved location)
    val defaultLocation = if (initialLocation != null) {
        GeoPoint(initialLocation.first, initialLocation.second)
    } else {
        GeoPoint(0.5142, 35.2697)
    }
    
    var currentCenter by remember { mutableStateOf(defaultLocation) }
    var mapView: MapView? by remember { mutableStateOf(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val userPoint = GeoPoint(location.latitude, location.longitude)
                        mapView?.controller?.animateTo(userPoint)
                        mapView?.controller?.setZoom(17.0)
                        currentCenter = userPoint
                    } else {
                        Toast.makeText(context, "Could not determine current location", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: SecurityException) {
                // Ignore, permission should be granted
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun performSearch(query: String) {
        if (query.isBlank()) return
        isSearching = true
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val results = geocoder.getFromLocationName(query, 1)
                
                withContext(Dispatchers.Main) {
                    isSearching = false
                    if (!results.isNullOrEmpty()) {
                        val address = results[0]
                        val searchPoint = GeoPoint(address.latitude, address.longitude)
                        mapView?.controller?.animateTo(searchPoint)
                        mapView?.controller?.setZoom(16.0)
                        currentCenter = searchPoint
                    } else {
                        Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isSearching = false
                    Toast.makeText(context, "Error searching location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pick Address", fontWeight = FontWeight.Bold, color = Primary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        try {
                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                if (location != null) {
                                    val userPoint = GeoPoint(location.latitude, location.longitude)
                                    mapView?.controller?.animateTo(userPoint)
                                    mapView?.controller?.setZoom(17.0)
                                    currentCenter = userPoint
                                } else {
                                    Toast.makeText(context, "Turn on device location", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: SecurityException) { }
                    } else {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                containerColor = Color.White,
                contentColor = Primary,
                modifier = Modifier.padding(bottom = 72.dp) // padding above info card
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = {
                        val centerMap = mapView?.mapCenter
                        val center = if (centerMap != null) GeoPoint(centerMap.latitude, centerMap.longitude) else currentCenter
                        onLocationSelected(center.latitude, center.longitude)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D17)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Confirm Location", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                        controller.setZoom(15.0)
                        controller.setCenter(defaultLocation)

                        val mapEventsReceiver = object : org.osmdroid.events.MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                if (p != null) {
                                    controller.animateTo(p)
                                }
                                return true
                            }
                            override fun longPressHelper(p: GeoPoint?): Boolean {
                                return false
                            }
                        }
                        overlays.add(org.osmdroid.views.overlay.MapEventsOverlay(mapEventsReceiver))

                        addMapListener(object : MapListener {
                            override fun onScroll(event: ScrollEvent?): Boolean {
                                val center = mapCenter
                                currentCenter = GeoPoint(center.latitude, center.longitude)
                                return false
                            }

                            override fun onZoom(event: ZoomEvent?): Boolean {
                                return false
                            }
                        })
                    }.also { mapView = it }
                }
            )

            // Lifecycle handling
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                        Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    mapView?.onDetach()
                }
            }
            
            // Search Bar overlay at the top
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .align(Alignment.TopCenter),
                placeholder = { Text("Search for an address...") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { performSearch(searchQuery) }),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Primary
                )
            )
            
            if (isSearching) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 72.dp)
                        .align(Alignment.TopCenter),
                    color = Primary
                )
            }
            
            // Center Marker
            Icon(
                Icons.Default.LocationOn,
                contentDescription = "Selected Location",
                tint = Color.Red,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
                    .offset(y = (-24).dp) // Offset to make the pin point at the exact center
            )
            
            // Location Info Card
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Move the map to set your location",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B3D17),
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lat: ${String.format(Locale.US, "%.4f", currentCenter.latitude)}, Lng: ${String.format(Locale.US, "%.4f", currentCenter.longitude)}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
