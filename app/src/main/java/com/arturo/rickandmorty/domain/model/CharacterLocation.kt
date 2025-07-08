package com.arturo.rickandmorty.domain.model

data class CharacterLocation(
    val id: String,
    val name: String,
    val status: String,
    val species: String,
    val location: String,
    val episodeIds: List<String>
)