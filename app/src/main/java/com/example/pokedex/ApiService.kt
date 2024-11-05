package com.example.pokedex

import com.example.pokedex.common.model.PokeDTO
import com.example.pokedex.common.model.PokeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @GET("pokemon?")
    fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Call<PokeResponse>

    @GET("pokemon/{id}")
    fun getPokemon(@Path("id") id: Int): Call<PokeDTO>

    companion object {
        const val IMAGE_BASE_URL =
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"
    }
}