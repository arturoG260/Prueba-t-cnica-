package com.arturo.rickandmorty.ui.map

import android.R
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import java.util.Random

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun LocationsMapScreen(
    onBackPressed: () -> Unit,
    viewModel: LocationsMapViewModel = hiltViewModel<LocationsMapViewModel>()
) {
    val context = LocalContext.current
    val characters by viewModel.characters.collectAsStateWithLifecycle()
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
            context.getSharedPreferences(
                "osmdroid",
                Context.MODE_PRIVATE
            )
        )

        onDispose {
            mapView.onPause()
        }
    }

    // Generate random locations for characters
    LaunchedEffect(characters) {
        if (characters.isNotEmpty()) {
            val random = Random(System.currentTimeMillis())
            val markers = characters.map { character ->
                // Generar coordenadas más distribuidas y realistas
                val lat = (-60.0 + random.nextDouble() * 120.0).coerceIn(-85.0, 85.0) // Entre -60 y 60, limitado a -85/85
                val lon = (-180.0 + random.nextDouble() * 360.0).coerceIn(-180.0, 180.0) // Cubre todo el rango longitudinal

                Marker(mapView).apply {
                    position = GeoPoint(lat, lon)
                    title = character.name
                    snippet = "Status: ${character.status}\nSpecies: ${character.species}\nLocation: ${character.location}"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
            }

            mapView.overlays.clear()
            markers.forEach { mapView.overlays.add(it) }

            // Zoom to show all markers
            mapView.zoomToBoundingBox(findBoundingBox(markers), false, 50)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Character Locations") },
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

@RequiresApi(Build.VERSION_CODES.N)
private fun findBoundingBox(markers: List<Marker>): org.osmdroid.util.BoundingBox {
    if (markers.isEmpty()) {
        return org.osmdroid.util.BoundingBox(0.0, 0.0, 0.0, 0.0)
    }

    var minLat = markers[0].position.latitude
    var maxLat = markers[0].position.latitude
    var minLon = markers[0].position.longitude
    var maxLon = markers[0].position.longitude

    markers.forEach { marker ->
        val lat = marker.position.latitude
        val lon = marker.position.longitude

        minLat = minOf(minLat, lat)
        maxLat = maxOf(maxLat, lat)
        minLon = minOf(minLon, lon)
        maxLon = maxOf(maxLon, lon)
    }

    return org.osmdroid.util.BoundingBox(maxLat, maxLon, minLat, minLon)
}