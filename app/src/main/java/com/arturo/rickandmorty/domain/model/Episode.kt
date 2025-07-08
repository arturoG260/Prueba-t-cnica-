package com.arturo.rickandmorty.domain.model

data class Episode(
    val id: String,
    val name: String,
    val airDate: String,
    val episodeCode: String,  // Ejemplo: "S01E01"
    val characterIds: List<String>  // IDs de los personajes
)