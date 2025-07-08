package com.arturo.rickandmorty.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.arturo.rickandmorty.navigation.RickAndMortyNavHost
import com.arturo.rickandmorty.ui.components.BottomNavigationBar

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->

        RickAndMortyNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}