package com.arturo.rickandmorty.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arturo.rickandmorty.data.repository.RickAndMortyRepository
import com.arturo.rickandmorty.domain.model.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RickAndMortyRepository
) : ViewModel() {

    private val _characterIds = MutableStateFlow<List<String>?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _statusFilter = MutableStateFlow<String?>(null)
    private val _speciesFilter = MutableStateFlow<String?>(null)
    private val _triggerRefresh = MutableStateFlow(0)

    val locations = repository.getLocations().cachedIn(viewModelScope)

    val characters: Flow<PagingData<Character>> = combine(
        _characterIds,
        _searchQuery,
        _statusFilter,
        _speciesFilter,
        _triggerRefresh
    ) { ids, query, status, species, _ ->
        FilterParams(ids, query, status, species)
    }.flatMapLatest { params ->
        when {
            !params.query.isEmpty() -> repository.searchCharacters(params.query)
            params.ids != null -> repository.getCharactersByLocation(params.ids)
            params.status != null || params.species != null ->
                repository.getCharactersWithFilters(
                    status = params.status,
                    species = params.species
                )
            else -> repository.getCharactersPaging()
        }
    }.cachedIn(viewModelScope)

    fun getMultipleCharacters(ids: List<String>) {
        _characterIds.value = ids
    }

    fun resetCharacters() {
        _characterIds.value = null
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        // Reset location filter when searching
        if (query.isNotEmpty()) {
            _characterIds.value = null
        }
    }

    fun onFiltersChanged(query: String, status: String?, species: String?) {
        _searchQuery.value = query
        _statusFilter.value = status
        _speciesFilter.value = species
        _triggerRefresh.value++ // Forzar actualización
    }

    fun refreshCharacters() {
        _triggerRefresh.value++
    }
}

private data class FilterParams(
    val ids: List<String>?,
    val query: String,
    val status: String?,
    val species: String?
)