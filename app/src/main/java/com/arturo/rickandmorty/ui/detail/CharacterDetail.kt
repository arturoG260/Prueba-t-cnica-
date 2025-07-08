package com.arturo.rickandmorty.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.arturo.rickandmorty.R
import com.arturo.rickandmorty.domain.model.Episode
import com.arturo.rickandmorty.ui.home.ShowSnackBar
import com.arturo.rickandmorty.util.formatDateString
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetail(
    characterId: String,
    navigateToBack: () -> Unit,
    navigateToMap: (String, String) -> Unit,
    viewModel: CharacterDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.getCharacter(characterId)
    }

    val character by viewModel.character.collectAsStateWithLifecycle()
    val episodes by viewModel.episodes.collectAsState()
    val viewedEpisodes by viewModel.viewedEpisodes.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    character.data?.let {
                        Text(text = it.name, fontFamily = FontFamily(Font(R.font.avenir_regular)))
                    }
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .clickable { navigateToBack() },
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.return_to_previous_screen)
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            character.let { uiState ->
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                } else if (uiState.errorMessage != null) {
                    snackBarHostState.ShowSnackBar(errorMessage = uiState.errorMessage)
                } else if (uiState.data != null) {
                    // Character Image (full width)
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                        model = uiState.data.image,
                        contentDescription = stringResource(id = R.string.character_image),
                        contentScale = ContentScale.Crop
                    )

                    // Character Attributes
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        AttributeRow(title = "Status: ", text = uiState.data.status)
                        AttributeRow(title = "Species: ", text = uiState.data.species)
                        AttributeRow(title = "Gender: ", text = uiState.data.gender)
                        AttributeRow(title = "Origin: ", text = uiState.data.origin)
                        AttributeRow(title = "Location: ", text = uiState.data.location)

                        // View on Map Button
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                navigateToMap(uiState.data.location, uiState.data.name)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "View on Map"
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(text = "View on Map")
                        }

                        // Episodes Section
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Episodes:",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.avenir_regular))
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        ) {
                            items(episodes.size) { episode ->
                                EpisodeItem(
                                    episode = episodes[episode],
                                    isViewed = viewedEpisodes.contains(episodes[episode].id),
                                    onToggleViewed = { viewModel.toggleEpisodeViewed(episodes[episode].id) }
                                )
                            }
                            if (episodes.size < (character.data?.episodeIds?.size ?: 0)) {
                                item {
                                    Button(
                                        onClick = { viewModel.loadNextEpisodes(character.data!!.episodeIds) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Cargar más episodios")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EpisodeItem(
    episode: Episode,
    isViewed: Boolean,
    onToggleViewed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleViewed() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${episode.episodeCode}: ${episode.name}",
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.avenir_regular))
                )
                Text(
                    text = "Aired: ${episode.airDate}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.avenir_regular))
                )
            }

            if (isViewed) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Viewed",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AttributeRow(modifier: Modifier = Modifier, title: String, text: String) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        TitleText(text = title)
        RegularText(text = text)
    }
}

@Composable
fun TitleText(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily(Font(R.font.avenir_regular))
    )
}

@Composable
fun RegularText(text: String) {
    Text(
        text = text.replaceFirstChar { it.uppercase() },
        fontSize = 18.sp,
        fontFamily = FontFamily(Font(R.font.avenir_regular))
    )
}

// Function to setup OpenStreetMap (call this from your Map screen)
fun setupMapView(mapView: MapView, locationName: String, characterName: String) {
    // Configure osmdroid
    Configuration.getInstance().userAgentValue = "com.arturo.rickandmorty"

    // Set initial position (in a real app, you would geocode the locationName)
    val startPoint = GeoPoint(0.0, 0.0) // Default position

    mapView.controller.setZoom(15.0)
    mapView.controller.setCenter(startPoint)

    // Add marker
    val marker = Marker(mapView)
    marker.position = startPoint
    marker.title = "$characterName - $locationName"
    mapView.overlays.add(marker)
}