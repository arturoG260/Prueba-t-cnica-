package com.arturo.rickandmorty.ui.detail

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    locationName: String,
    characterName: String,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setBuiltInZoomControls(true)
            setMultiTouchControls(true)
        }
    }

    // Configure osmdroid
    DisposableEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )

        onDispose {
            // Clean up map resources
            mapView.onPause()
        }
    }

    // Set map location and marker
    DisposableEffect(locationName, characterName) {
        // In a real app, you would geocode the locationName to get coordinates
        // For demo purposes, we'll use a default location
        val random = Random(System.currentTimeMillis())
        // Generar coordenadas más distribuidas y realistas
        val lat = (-60.0 + random.nextDouble() * 120.0).coerceIn(-85.0, 85.0) // Entre -60 y 60, limitado a -85/85
        val lon = (-180.0 + random.nextDouble() * 360.0).coerceIn(-180.0, 180.0) // Cubre todo el rango longitudinal

        val defaultLocation = GeoPoint(lat, lon)

        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(defaultLocation)

        // Add marker
        val marker = Marker(mapView)
        marker.position = defaultLocation
        marker.title = "$characterName - $locationName"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.clear()
        mapView.overlays.add(marker)

        onDispose { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$characterName's Location") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// Preview for Android Studio
@OptIn(ExperimentalMaterial3Api::class)
class MapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationName = intent.getStringExtra("location") ?: "Earth"
        val characterName = intent.getStringExtra("character") ?: "Character"

        setContent {
            MapScreen(
                locationName = locationName,
                characterName = characterName,
                onBackPressed = { finish() }
            )
        }
    }
}