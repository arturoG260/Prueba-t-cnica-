package com.arturo.rickandmorty.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.arturo.rickandmorty.navigation.RickAndMortyNavHost
import com.arturo.rickandmorty.ui.theme.RickAndMortyTheme
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = packageName
        setContent {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            enableEdgeToEdge()
            RickAndMortyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    MainScreen(navController = navController)
                }
            }
        }
    }
}