package com.example.pokedex.detail.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokedex.R
import com.example.pokedex.common.model.Ability
import com.example.pokedex.common.model.AbilityDetail
import com.example.pokedex.common.model.PokeDTO
import com.example.pokedex.common.model.Stat
import com.example.pokedex.common.model.StatDetail
import com.example.pokedex.common.model.Type
import com.example.pokedex.common.model.TypeDetail
import com.example.pokedex.detail.data.DetailService
import com.example.pokedex.detail.presentation.PokeDetailViewModel
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokeDetailScreen(
    pokemonId: String?,
    navController: NavController
) {

    val viewModel: PokeDetailViewModel = viewModel(
        factory = PokeDetailViewModel.provideFactory(pokemonId ?: "")
    )


    val pokemonDetail by viewModel.uiPokemonDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {

        //Header
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        ) {

                            Text(
                                text = "Pokédex",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "#${pokemonDetail?.let { String.format("%03d", it.id) }}",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .width(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back),
                                contentDescription = "Back"
                            )
                        }

                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .height(50.dp)

                )
            }, content = {
                PokeDetailContent(pokemonDetail = pokemonDetail, isLoading = isLoading)
            }
        )
    }
}


@SuppressLint("DefaultLocale")
@Composable
private fun PokeDetailContent(pokemonDetail: PokeDTO?, isLoading: Boolean) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = Color(0xFFE3350D),
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

            //Seção Imagem
            val backgroundColor = getBackgroundColor(pokemonDetail.types)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
                    .height(200.dp)
                    .background(color = backgroundColor, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "${DetailService.IMAGE_BASE_URL}${pokemonDetail.id}.png",
                    contentDescription = "${pokemonDetail.name} image",
                    modifier = Modifier.size(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            //Seção Nome
            Text(
                text = pokemonDetail.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                },
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            //Seção Tipos
            if (pokemonDetail.types.size == 1) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Chip(
                        label = pokemonDetail.types[0].type.name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        },
                        backgroundColor = getTypeColor(pokemonDetail.types[0].type.name),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 60.dp),
                ) {
                    pokemonDetail.types.forEach { type ->
                        Chip(
                            label = type.type.name.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            },
                            backgroundColor = getTypeColor(type = type.type.name),
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .width(100.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            //Seção peso e altura
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${pokemonDetail.weight / 10.0}" + " KG",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "weight",
                        style = MaterialTheme.typography.bodySmall, color = Color.Gray,
                        textAlign = TextAlign.Center

                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${pokemonDetail.height / 10.0}" + " M",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "height",
                        style = MaterialTheme.typography.bodySmall, color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
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
                StatRow(
                    name = "SP.ATK",
                    value = pokemonDetail.stats.find { it.stat.name == "special-attack" }?.baseStat
                        ?: 0
                )
                StatRow(
                    name = "SP.DEF",
                    value = pokemonDetail.stats.find { it.stat.name == "special-defense" }?.baseStat
                        ?: 0
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
fun Chip(label: String, backgroundColor: Color, modifier: Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 5.dp),

        ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun getBackgroundColor(types: List<Type>): Color {
    return if (types.isNotEmpty()) {
        getTypeColor(types[0].type.name)
    } else {
        Color.LightGray
    }
}

@Composable
fun getTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "fire" -> Color(0xFFE98160)
        "water" -> Color(0xFF72C7EE)
        "grass" -> Color(0xFFB0F188)
        "electric" -> Color(0xFFFFEE58)
        "ice" -> Color(0xFF34ABBB)
        "fighting" -> Color(0xFFF5635B)
        "poison" -> Color(0xFFA040A0)
        "ground" -> Color(0xFFE0C068)
        "flying" -> Color(0xFFA890F0)
        "psychic" -> Color(0xFFF85888)
        "bug" -> Color(0xFFA8B820)
        "rock" -> Color(0xFF747370)
        "ghost" -> Color(0xFF705898)
        "dragon" -> Color(0xFF7038F8)
        "dark" -> Color(0xFF292523)
        "steel" -> Color(0xFFB8B8D0)
        "fairy" -> Color(0xFFEE99AC)
        else -> Color.Gray
    }
}

@Composable
fun StatRow(name: String, value: Int) {
    var animatedValue by remember { mutableIntStateOf(0) }
    val progress by animateFloatAsState(
        targetValue = animatedValue / 200f,
        animationSpec = tween(
            durationMillis = 3000,  // Duração da animação
            easing = LinearOutSlowInEasing
        ), label = ""
    )

    LaunchedEffect(key1 = value) {
        val steps = 300
        for (i in 0..steps) {
            animatedValue = (value * i / steps)
            kotlinx.coroutines.delay(300L / steps)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.width(50.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(16.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color.LightGray)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(RoundedCornerShape(9.dp))
                    .background(
                        when (name) {
                            "HP" -> Color(0xFFE94040)
                            "ATK" -> Color(0xFFFFA500)
                            "SP.ATK" -> Color(0xFF705898)
                            "DEF" -> Color(0xFF1977C9)
                            "SP.DEF" -> Color(0xFFEE99AC)
                            "SPD" -> Color(0xFF9DDCF5)
                            else -> Color.Green
                        }
                    )
                    .animateContentSize()
            )
            Text(
                text = "$animatedValue/200",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }
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
                Stat(StatDetail("special-attack"), 90),
                Stat(StatDetail("defense"), 78),
                Stat(StatDetail("special-defense"), 80),
                Stat(StatDetail("speed"), 100),

                ),
            moves = emptyList(),
            forms = emptyList(),
            heldItems = emptyList(),
            baseExperience = 64
        ),
        isLoading = false
    )
}


