package com.arturo.rickandmorty.util

import com.arturo.rickandmorty.R

sealed class Screen(val route: String, val title: Int, val icon: Int) {
    object Splash : Screen("splash", R.string.splash, R.drawable.ic_splash)
    object Home : Screen("Inicio", R.string.home, R.drawable.ic_home)
    object CharacterDetail : Screen("character_detail", R.string.character_detail, R.drawable.ic_character)
    object Map : Screen("Mapa Mundial", R.string.map, R.drawable.ic_map)
}