package com.arturo.rickandmorty.domain.model

data class Character(
    val id: String,
    val name: String,
    val image: String,
    val gender: String,
    val status: String,
    val species: String,
    val origin: String,
    val location: String,
    val episodes: String,
    val created: String,
    val episodeUrls: List<String>
){
    // Propiedad calculada para obtener los IDs
    val episodeIds: List<String> get() = episodeUrls.map { it.substringAfterLast("/") }
}