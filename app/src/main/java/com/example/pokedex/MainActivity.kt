package com.example.pokedex

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pokedex.ui.theme.PokedexTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false
        window.statusBarColor = 0xFFDB4623.toInt()
        setContent {
            PokedexTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "PokeList"
                ) {
                    composable(route = "PokeList") {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = { Text(text = "Pokédex") },
                                    navigationIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ball_icon),
                                            contentDescription = "Pokeball Icon",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .padding(start = 16.dp)
                                                .size(36.dp)
                                        )
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = Color(0xFFE3350D),
                                        titleContentColor = Color.White
                                    )
                                )
                            },
                            content = { paddingValues ->
                                Column(modifier = Modifier.padding(paddingValues)) {
                                    HorizontalDivider(
                                        modifier = Modifier.fillMaxWidth(),
                                        thickness = 2.dp,
                                        color = Color.Black
                                    )
                                    PokeListScreen(navController)
                                }
                            }
                        )
                    }
                    composable(route = "PokeDetail/{pokemonId}") { backStackEntry ->
                        val pokemonId = backStackEntry.arguments?.getString("pokemonId")
                        Scaffold(
                            topBar = { /* Sem Toolbar para a tela de detalhes */ },
                            content = { paddingValues ->
                                Column(modifier = Modifier.padding(paddingValues)) {
                                    PokeDetailScreen(pokemonId, navController)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
