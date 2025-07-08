package com.arturo.rickandmorty.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.arturo.rickandmorty.ui.detail.CharacterDetail
import com.arturo.rickandmorty.ui.detail.MapScreen
import com.arturo.rickandmorty.ui.home.Home
import com.arturo.rickandmorty.ui.splash.SplashScreen
import com.arturo.rickandmorty.util.Screen
import com.arturo.rickandmorty.ui.map.LocationsMapScreen

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun RickAndMortyNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Screen.Home.route
        } else {
            Screen.Splash.route
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                navigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            Home(
                navigateToCharacterDetail = { characterId ->
                    navController.navigate(Screen.CharacterDetail.route + "/$characterId")
                }
            )
        }
        composable(Screen.CharacterDetail.route + "/{characterId}") {
            it.arguments?.getString("characterId")?.let { characterId ->
                CharacterDetail(
                    characterId = characterId,
                    navigateToBack = { navController.popBackStack() },
                    navigateToMap = { location, name ->
                        navController.navigate(Screen.CharacterDetail.route + "/$location/$name")
                    }
                )
            }
        }
        composable(
            route = Screen.CharacterDetail.route + "/{location}/{name}",
            arguments = listOf(
                navArgument("location") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val location = backStackEntry.arguments?.getString("location") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            MapScreen(
                locationName = location,
                characterName = name,
                onBackPressed = { navController.popBackStack() }
            )
        }
        composable(Screen.Map.route) {
            LocationsMapScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}