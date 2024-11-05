package com.example.pokedex.detail.data

import com.example.pokedex.common.model.PokeDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface DetailService {
    @GET("pokemon/{id}")
    suspend fun getPokemon(@Path("id") id: Int): PokeDTO

    companion object {
        const val IMAGE_BASE_URL =
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"
    }
}