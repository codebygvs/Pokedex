package com.example.pokedex

import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun PokeApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "PokeList"
    ) {
        composable(route = "PokeList") {
            PokeListScreen(navController)
        }
        composable(route = "PokeDetail/{pokemonId}") { backStackEntry ->
            val pokemonId =
                backStackEntry.arguments?.getString("pokemonId")
            PokeDetailScreen(pokemonId, navController)
        }
    }
}