package com.arturo.rickandmorty.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arturo.rickandmorty.data.repository.RickAndMortyRepository
import com.arturo.rickandmorty.domain.model.Character
import com.arturo.rickandmorty.domain.model.Episode
import com.arturo.rickandmorty.ui.util.UiState
import com.arturo.rickandmorty.util.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(private val repository: RickAndMortyRepository) :
    ViewModel() {

    private val _character = MutableStateFlow<UiState<Character>>(UiState.initial())
    val character: StateFlow<UiState<Character>> get() = _character

    private val _episodes = MutableStateFlow<List<Episode>>(emptyList())
    val episodes: StateFlow<List<Episode>> get() = _episodes

    private val _viewedEpisodes = MutableStateFlow<Set<String>>(emptySet())
    val viewedEpisodes: StateFlow<Set<String>> get() = _viewedEpisodes

    private val _episodePage = MutableStateFlow(1)
    val episodePage: StateFlow<Int> get() = _episodePage

    fun loadEpisodes(episodeIds: List<String>, page: Int = 1) {
        viewModelScope.launch {
            val pageSize = 10
            val startIndex = (page - 1) * pageSize
            val endIndex = minOf(startIndex + pageSize, episodeIds.size)
            val paginatedIds = episodeIds.subList(startIndex, endIndex)

            if (paginatedIds.isEmpty()) return@launch

            repository.getEpisodes(paginatedIds).collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _episodes.value = if (page == 1) {
                            response.data ?: emptyList()
                        } else {
                            _episodes.value + (response.data ?: emptyList())
                        }
                        _episodePage.value = page
                    }
                    is NetworkResponse.Error -> {
                        // Manejar error (puedes exponerlo a la UI)
                        println("Error al cargar episodios: ${response.errorMessage}")
                    }
                    else -> {}
                }
            }
        }
    }

    fun loadNextEpisodes(episodeIds: List<String>) {
        loadEpisodes(episodeIds, _episodePage.value + 1)
    }

    fun getCharacter(id: String) {
        viewModelScope.launch {
            repository.getCharacter(id).collect { response ->
                when (response) {
                    is NetworkResponse.Loading -> {
                        _character.value = UiState<Character>().copy(isLoading = true)
                    }
                    is NetworkResponse.Error -> {
                        _character.value = UiState<Character>().copy(errorMessage = response.errorMessage)
                    }
                    is NetworkResponse.Success -> {
                        _character.value = UiState<Character>().copy(data = response.data)
                        response.data?.episodeIds?.let { ids -> // Usamos episodeIds aquí
                            loadEpisodes(ids)
                        }
                    }
                }
            }
        }
    }

    fun toggleEpisodeViewed(episodeId: String) {
        _viewedEpisodes.update { currentSet ->
            if (currentSet.contains(episodeId)) {
                currentSet - episodeId
            } else {
                currentSet + episodeId
            }
        }
    }
}