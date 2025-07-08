package com.arturo.rickandmorty.data.dto

import com.google.gson.annotations.SerializedName

data class EpisodeResponse(
    val id: String,
    val name: String,
    @SerializedName("air_date")
    val airDate: String,
    val episode: String,  // Ejemplo: "S01E01"
    val characters: List<String>,  // URLs de los personajes
    val url: String,
    val created: String
)