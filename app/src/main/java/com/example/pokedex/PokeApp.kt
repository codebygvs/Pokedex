package com.example.pokedex

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pokedex.detail.presentation.PokeDetailViewModel
import com.example.pokedex.detail.presentation.ui.PokeDetailScreen
import com.example.pokedex.list.presentation.PokeListViewModel
import com.example.pokedex.list.presentation.ui.PokeListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokeApp(
    listViewModel: PokeListViewModel
) {
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
                        PokeListScreen(navController, listViewModel)
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
