package com.arturo.rickandmorty.data.dto

import com.arturo.rickandmorty.domain.model.Character
import com.google.gson.annotations.SerializedName

data class CharacterResponse(
    @SerializedName("created")
    val created: String?,
    @SerializedName("episode")
    val episode: List<String>?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("location")
    val location: LocationOrOrigin?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("origin")
    val origin: LocationOrOrigin?,
    @SerializedName("species")
    val species: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("url")
    val url: String?
)

data class LocationOrOrigin(
    @SerializedName("name")
    val name: String?,
    @SerializedName("url")
    val url: String?
)

fun CharacterResponse.toCharacter(): Character {
    return Character(
        id = id?.toString() ?: "",
        name = name ?: "Unknown",
        image = image ?: "",
        gender = gender ?: "Unknown",
        status = status ?: "Unknown",
        species = species ?: "Unknown",
        origin = origin?.name ?: "Unknown",
        location = location?.name ?: "Unknown",
        episodes = episode?.size?.toString() ?: "0", // Convertir lista de episodios a cantidad
        created = created ?: "",
        episodeUrls = episode?.map { episodeUrl ->
            episodeUrl.substringAfterLast("/")} ?: listOf()
    )
}