package com.arturo.rickandmorty.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arturo.rickandmorty.data.repository.RickAndMortyRepository
import com.arturo.rickandmorty.domain.model.CharacterLocation
import com.arturo.rickandmorty.util.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsMapViewModel @Inject constructor(
    private val repository: RickAndMortyRepository
) : ViewModel() {
    private val _characters = MutableStateFlow<List<CharacterLocation>>(emptyList())
    val characters: StateFlow<List<CharacterLocation>> get() = _characters

    init {
        loadCharacters()
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            repository.getAllCharacters().collect { response ->
                when (response) {
                    is NetworkResponse.Success -> {
                        _characters.value = response.data?.map { character ->
                            CharacterLocation(
                                id = character.id,
                                name = character.name,
                                status = character.status,
                                species = character.species,
                                location = character.location,
                                episodeIds = character.episodeIds
                            )
                        } ?: emptyList()
                    }
                    else -> {} // Handle other states if needed
                }
            }
        }
    }
}
