package com.arturo.rickandmorty.data.repository

import androidx.paging.PagingData
import com.arturo.rickandmorty.data.dto.CharacterResponse
import com.arturo.rickandmorty.data.dto.RickAndMortyResponse
import com.arturo.rickandmorty.domain.model.Character
import com.arturo.rickandmorty.domain.model.Episode
import com.arturo.rickandmorty.domain.model.Location
import com.arturo.rickandmorty.util.NetworkResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RickAndMortyRepository {
    fun getCharactersPaging(): Flow<PagingData<Character>>
    fun getLocations(): Flow<PagingData<Location>>
    fun getCharactersByLocation(characterIds: List<String>): Flow<PagingData<Character>>
    fun searchCharacters(name: String): Flow<PagingData<Character>>
    fun getCharacter(id: String): Flow<NetworkResponse<Character>>
    fun getCharactersWithFilters(status: String?, species: String?): Flow<PagingData<Character>>
    fun getAllCharacters(): Flow<NetworkResponse<List<Character>>>
    fun getEpisodes(episodeIds: List<String>): Flow<NetworkResponse<List<Episode>>>
}