package com.arturo.rickandmorty.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.arturo.rickandmorty.R
import com.arturo.rickandmorty.util.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Map
    )

    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = stringResource(id = screen.title)
                    )
                },
                label = { Text(stringResource(id = screen.title)) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Evita múltiples copias de la misma pantalla
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}