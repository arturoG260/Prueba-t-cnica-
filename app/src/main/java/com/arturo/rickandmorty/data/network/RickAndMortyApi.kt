package com.arturo.rickandmorty.data.network

import com.arturo.rickandmorty.data.dto.CharacterResponse
import com.arturo.rickandmorty.data.dto.EpisodeResponse
import com.arturo.rickandmorty.data.dto.LocationResponse
import com.arturo.rickandmorty.data.dto.RickAndMortyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int,
                              @Query("status") status: String? = null,
                              @Query("species") species: String? = null): Response<RickAndMortyResponse<CharacterResponse>>

    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: String): Response<CharacterResponse>

    @GET("character/{ids}")
    suspend fun getMultipleCharacters(@Path("ids") ids: String): Response<List<CharacterResponse>>

    @GET("location")
    suspend fun getLocations(@Query("page") page: Int): Response<RickAndMortyResponse<LocationResponse>>

    @GET("character/")
    suspend fun searchCharacters(
        @Query("name") name: String,
        @Query("page") page: Int? = null
    ): Response<RickAndMortyResponse<CharacterResponse>>

    @GET("episode/{ids}")
    suspend fun getEpisodes(@Path("ids") ids: String): Response<List<EpisodeResponse>>
}