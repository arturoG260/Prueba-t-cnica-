package com.arturo.rickandmorty.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.arturo.rickandmorty.R
import com.arturo.rickandmorty.domain.model.Character
import com.arturo.rickandmorty.ui.theme.Yellow
import com.arturo.rickandmorty.util.CharacterGender
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navigateToCharacterDetail: (String) -> Unit) {
    val viewModel: HomeViewModel = hiltViewModel()
    val snackBarHostState = remember { SnackbarHostState() }
    val characterPagingItems = viewModel.characters.collectAsLazyPagingItems()
    var selectedLocation by remember { mutableIntStateOf(-1) }
    var searchQuery by remember { mutableStateOf("") }

    var showFilterDialog by remember { mutableStateOf(false) }
    var tempSelectedStatus by remember { mutableStateOf<String?>(null) }
    var tempSelectedSpecies by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var selectedSpecies by remember { mutableStateOf<String?>(null) }

    // Estado para el SwipeRefresh
    val isRefreshing = rememberSwipeRefreshState(
        isRefreshing = characterPagingItems.loadState.refresh is LoadState.Loading
    )

    // Manejar el estado de refresco
    LaunchedEffect(characterPagingItems.loadState.refresh) {
        if (characterPagingItems.loadState.refresh !is LoadState.Loading) {
            isRefreshing.isRefreshing = false
        }
    }

    // Observar cambios en los filtros
    LaunchedEffect(searchQuery, selectedStatus, selectedSpecies) {
        viewModel.onFiltersChanged(searchQuery, selectedStatus, selectedSpecies)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search characters...") },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent
                                ),
                                singleLine = true
                            )

                            IconButton(
                                onClick = { showFilterDialog = true },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = "Filters",
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Diálogo de filtros
                            if (showFilterDialog) {
                                AlertDialog(
                                    onDismissRequest = {
                                        showFilterDialog = false
                                        // Restablecer los valores temporales al cancelar
                                        tempSelectedStatus = selectedStatus
                                        tempSelectedSpecies = selectedSpecies
                                    },
                                    title = { Text("Filtrar personajes") },
                                    text = {
                                        Column {
                                            // Selector de Status
                                            Text("Estado:", style = MaterialTheme.typography.bodyLarge)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                listOf(null, "Alive", "Dead", "unknown").forEach { status ->
                                                    FilterChip(
                                                        selected = tempSelectedStatus == status,
                                                        onClick = { tempSelectedStatus = status },
                                                        label = { Text(status ?: "Todos") }
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(16.dp))

                                            // Selector de Species
                                            Text("Especie:", style = MaterialTheme.typography.bodyLarge)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                listOf(null, "Human", "Alien", "Robot").forEach { species ->
                                                    FilterChip(
                                                        selected = tempSelectedSpecies == species,
                                                        onClick = { tempSelectedSpecies = species },
                                                        label = { Text(species ?: "Todos") }
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                // Aplicar los filtros temporales a los reales
                                                selectedStatus = tempSelectedStatus
                                                selectedSpecies = tempSelectedSpecies
                                                showFilterDialog = false
                                                // Forzar la actualización
                                                viewModel.onFiltersChanged(searchQuery, selectedStatus, selectedSpecies)
                                            }
                                        ) {
                                            Text("Aplicar")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(
                                            onClick = {
                                                // Limpiar todos los filtros
                                                tempSelectedStatus = null
                                                tempSelectedSpecies = null
                                                selectedStatus = null
                                                selectedSpecies = null
                                                showFilterDialog = false
                                                // Forzar la actualización
                                                viewModel.onFiltersChanged(searchQuery, null, null)
                                            }
                                        ) {
                                            Text("Limpiar")
                                        }
                                    }
                                )
                            }

                            LaunchedEffect(showFilterDialog) {
                                if (showFilterDialog) {
                                    tempSelectedStatus = selectedStatus
                                    tempSelectedSpecies = selectedSpecies
                                }
                            }
                            if (selectedStatus != null || selectedSpecies != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    selectedStatus?.let { status ->
                                        FilterChip(
                                            selected = true,
                                            onClick = { selectedStatus = null },
                                            label = { Text("Status: $status") },
                                            trailingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Clear status filter"
                                                )
                                            }
                                        )
                                    }
                                    selectedSpecies?.let { species ->
                                        FilterChip(
                                            selected = true,
                                            onClick = { selectedSpecies = null },
                                            label = { Text("Species: $species") },
                                            trailingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Clear species filter"
                                                )
                                            }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                        }
                    },
                    actions = {
                        if (selectedLocation != -1) {
                            TextButton(
                                onClick = {
                                    selectedLocation = -1
                                    viewModel.resetCharacters()
                                }
                            ) {
                                Text(text = "Clear Filter")
                            }
                        }
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(16.dp),
                hostState = snackBarHostState
            ) { data ->
                Snackbar(
                    action = {
                        TextButton(
                            onClick = { characterPagingItems.retry() }
                        ) {
                            Text(text = stringResource(R.string.retry))
                        }
                    }
                ) {
                    Text(text = data.visuals.message)
                }
            }
        }
    ) { paddingValues ->
        SwipeRefresh(
            state = isRefreshing,
            onRefresh = {
                characterPagingItems.refresh()
                viewModel.refreshCharacters()
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        start = 16.dp,
                        end = 16.dp
                    )
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Lista de personajes
                when (characterPagingItems.loadState.refresh) {
                    is LoadState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is LoadState.Error -> {
                        val error = characterPagingItems.loadState.refresh as LoadState.Error
                        LaunchedEffect(snackBarHostState) {
                            snackBarHostState.showSnackbar(
                                message = error.error.localizedMessage ?: "Error loading characters",
                                duration = SnackbarDuration.Long
                            )
                        }
                    }

                    else -> {
                        CharacterPagedList(
                            pagingItems = characterPagingItems,
                            onItemClick = navigateToCharacterDetail,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

fun Modifier.Companion.align(alignment: Alignment): Modifier {
    val modifier = Modifier.align(alignment)
    return modifier
}

@Composable
private fun LocationFilterRow(
    viewModel: HomeViewModel,
    selectedLocation: Int,
    onLocationSelected: (Int, List<String>) -> Unit
) {
    val locationPagingItems = viewModel.locations.collectAsLazyPagingItems()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(locationPagingItems.itemSnapshotList.items) { index, location ->
            location.let {
                LocationButton(
                    text = it.name,
                    isSelected = selectedLocation == index,
                    onClick = {
                        onLocationSelected(index, it.residents)
                    }
                )
            }
        }

        when (locationPagingItems.loadState.append) {
            is LoadState.Loading -> {
                item {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }

            is LoadState.Error -> {
                item {
                    Text(
                        text = "Error loading locations",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun CharacterPagedList(
    pagingItems: LazyPagingItems<Character>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(pagingItems.itemCount) { character ->
            character.let {
                CharacterCard(
                    character = pagingItems[it]!!,
                    onClick = { onItemClick(pagingItems[it]!!.id) }
                )
            }
        }

        when (pagingItems.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }

            is LoadState.Error -> {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error loading more characters",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = { pagingItems.retry() },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = "Retry")
                        }
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun CharacterCard(
    character: Character,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        val backgroundColor = when (character.gender.uppercase()) {
            CharacterGender.MALE.name -> Color.Cyan.copy(alpha = 0.2f)
            CharacterGender.FEMALE.name -> Color(0xFFFF7AA7).copy(alpha = 0.2f)
            CharacterGender.GENDERLESS.name -> Color.Yellow.copy(alpha = 0.2f)
            else -> Color.LightGray.copy(alpha = 0.2f)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                modifier = Modifier.size(80.dp),
                model = character.image,
                contentDescription = "Character image: ${character.name}",
                contentScale = ContentScale.Crop
            )

            Column {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Status: ${character.status}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Species: ${character.species}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Episodes: ${character.episodes}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun LocationButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = if (isSelected) {
        ButtonDefaults.buttonColors(
            containerColor = Yellow,
            contentColor = Color.Black
        )
    } else {
        ButtonDefaults.buttonColors()
    }

    Button(
        onClick = onClick,
        colors = colors
    ) {
        Text(text = text)
    }
}

@Composable
fun SnackbarHostState.ShowSnackBar(
    errorMessage: String?,
    onRetry: (() -> Unit)? = null
) {
    errorMessage?.let { message ->
        LaunchedEffect(key1 = message) {
            val result = showSnackbar(
                message = message,
                actionLabel = onRetry?.let { "Retry" },
                duration = SnackbarDuration.Long
            )

            if (result == SnackbarResult.ActionPerformed) {
                onRetry?.invoke()
            }
        }
    }
}