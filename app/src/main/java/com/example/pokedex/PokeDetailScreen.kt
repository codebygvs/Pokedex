package com.example.pokedex

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokedex.model.Ability
import com.example.pokedex.model.AbilityDetail
import com.example.pokedex.model.PokeDTO
import com.example.pokedex.model.Stat
import com.example.pokedex.model.StatDetail
import com.example.pokedex.model.Type
import com.example.pokedex.model.TypeDetail
import retrofit2.Call
import retrofit2.Response


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokeDetailScreen(pokemonId: String?, navController: NavController) {
    val apiService = RetrofitClient.retrofitInstance.create(ApiService::class.java)
    var pokemonDetail by remember { mutableStateOf<PokeDTO?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(pokemonId) {
        val id = pokemonId?.toIntOrNull()
        if (id != null) {
            val call: Call<PokeDTO> = apiService.getPokemon(id)
            call.enqueue(object : retrofit2.Callback<PokeDTO> {
                override fun onResponse(call: Call<PokeDTO>, response: Response<PokeDTO>) {
                    if (response.isSuccessful) {
                        pokemonDetail = response.body()
                    }
                    isLoading = false
                }

                override fun onFailure(call: Call<PokeDTO>, t: Throwable) {
                    // Handle error
                    isLoading = false
                }
            })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Pokédex", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "#${pokemonDetail?.let { String.format("%03d", it.id) }}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }

                }
            )
        }, content = {
            PokeDetailContent(pokemonDetail = pokemonDetail, isLoading = isLoading)
        }
    )
}


@SuppressLint("DefaultLocale")
@Composable
private fun PokeDetailContent(pokemonDetail: PokeDTO?, isLoading: Boolean) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = Color(0xFFE3350D), // Vermelho Pokémon
                strokeWidth = 4.dp
            )
        }
    } else if (pokemonDetail != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //cabeçalho

            //Seção Imagem
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "${ApiService.IMAGE_BASE_URL}${pokemonDetail.id}.png",
                    contentDescription = "${pokemonDetail.name} image",
                    modifier = Modifier.size(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            //Seção Nome
            Text(
                text = pokemonDetail.name.capitalize(),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Seção Tipos
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                pokemonDetail.types.forEach { type ->
                    Chip(label = type.type.name.capitalize())
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            //Seção peso e altura
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Weight: ${pokemonDetail.weight / 10.0} kg")
                Text(text = "Height: ${pokemonDetail.height / 10.0} m")
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Seção Status
            Column {
                StatRow(
                    name = "HP",
                    value = pokemonDetail.stats.find { it.stat.name == "hp" }?.baseStat ?: 0
                )
                StatRow(
                    name = "ATK",
                    value = pokemonDetail.stats.find { it.stat.name == "attack" }?.baseStat ?: 0
                )
                StatRow(
                    name = "DEF",
                    value = pokemonDetail.stats.find { it.stat.name == "defense" }?.baseStat
                        ?: 0
                )
                StatRow(
                    name = "SPD",
                    value = pokemonDetail.stats.find { it.stat.name == "speed" }?.baseStat ?: 0
                )
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No Pokémon details available.")
        }
    }
}


@Composable
fun Chip(label: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.LightGray)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun StatRow(name: String, value: Int) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = name, style = MaterialTheme.typography.bodySmall)
        LinearProgressIndicator(
            progress = { value / 300f },
            modifier = Modifier.width(150.dp)
        )
        Text(text = "$value/300", style = MaterialTheme.typography.bodySmall)

    }
}

@Preview(showBackground = true)
@Composable
private fun PokeDetailPreview() {
    PokeDetailContent(
        pokemonDetail = PokeDTO(
            id = 1,
            name = "Bulbasaur",
            height = 7,
            weight = 69,
            types = listOf(Type(TypeDetail("flying")), Type(TypeDetail("fire"))),
            abilities = listOf(
                Ability(
                    AbilityDetail(
                        "Overgrow",
                        "https://pokeapi.co/api/v2/ability/7/"
                    ), false, 1
                )
            ),
            stats = listOf(
                Stat(StatDetail("hp"), 78),
                Stat(StatDetail("attack"), 84),
                Stat(StatDetail("defense"), 78),
                Stat(StatDetail("speed"), 100)
            ),
            moves = emptyList(),
            forms = emptyList(),
            heldItems = emptyList()
        ),
        isLoading = false
    )
}


