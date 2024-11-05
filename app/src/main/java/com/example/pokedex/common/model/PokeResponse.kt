package com.example.pokedex.common.model

import com.google.gson.annotations.SerializedName

data class PokeResponse(
    @SerializedName("results")
    val resultsPoke: List<SimplePokemon>
)

data class SimplePokemon(
    val name: String,
    val url: String
) {
    val id: Int
        get() = url.split("/").last { it.isNotEmpty() }.toInt()
}

