package com.example.pokedex

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.pokedex.model.PokeDTO
import com.example.pokedex.model.PokeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun PokeListScreen(navController: NavHostController) {
    var pokemonList by remember { mutableStateOf<List<PokeDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val apiService = RetrofitClient.retrofitInstance.create(ApiService::class.java)

    LaunchedEffect(Unit) {
        val callPokemonList = apiService.getPokemonList(limit = 100, offset = 0)
        callPokemonList.enqueue(object : Callback<PokeResponse> {
            override fun onResponse(
                call: Call<PokeResponse>,
                response: Response<PokeResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.resultsPoke?.forEach { pokemon ->
                        val callPokemon: Call<PokeDTO> =
                            apiService.getPokemon(pokemon.id)
                        callPokemon.enqueue(object : Callback<PokeDTO> {
                            override fun onResponse(
                                call: Call<PokeDTO>,
                                response: Response<PokeDTO>
                            ) {
                                if (response.isSuccessful) {
                                    val detailedPokemon = response.body()
                                    if (detailedPokemon != null) {
                                        pokemonList =
                                            (pokemonList + detailedPokemon).sortedBy { it.id }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<PokeDTO>, t: Throwable) {
                                // Handle error
                            }
                        })
                    }
                }
                isLoading = false
            }

            override fun onFailure(call: Call<PokeResponse>, t: Throwable) {
                // Handle error
                isLoading = false
            }
        })
    }

    PokeListContent(
        pokemonList = pokemonList,
        isLoading = isLoading,
        onClick = { pokemon ->
            navController.navigate(route = "PokeDetail/${pokemon.id}")
        }
    )
}

@Composable
private fun PokeListContent(
    pokemonList: List<PokeDTO>,
    isLoading: Boolean,
    onClick: (PokeDTO) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFE3350D), // Vermelho Pokémon
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Loading...")
                    }
                }
            }

            else -> {
                if (pokemonList.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(pokemonList.size) { index ->
                            val pokemon = pokemonList[index]
                            PokemonItem(
                                pokemonDTO = pokemon,
                                imageBaseUrl = ApiService.IMAGE_BASE_URL,
                                onClick = onClick
                            )

                        }

                    }
                }
            }
        }
    }
}


@SuppressLint("DefaultLocale")
@Composable
private fun PokemonItem(
    pokemonDTO: PokeDTO,
    imageBaseUrl: String,
    onClick: (PokeDTO) -> Unit,
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .clickable { onClick.invoke(pokemonDTO) }
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray)
            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(12.dp))
            .padding(16.dp)
            .width(180.dp)
            .height(240.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            AsyncImage(
                model = "$imageBaseUrl${pokemonDTO.id}.png",
                contentDescription = "${pokemonDTO.name} Pokémon image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .align(Alignment.Center),
                contentScale = ContentScale.Crop,
                onSuccess = { isLoading = false },
                onLoading = { isLoading = true },
                onError = { isLoading = false }
            )
            if (isLoading) {
                Icon(
                    painter = painterResource(id = R.drawable.ball_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(100.dp)
                        .alpha(0.3f),
                    tint = Color.Black
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "#${String.format("%03d", pokemonDTO.id)}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = pokemonDTO.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
