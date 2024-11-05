package com.example.pokedex.list.data

import com.example.pokedex.common.model.PokeDTO
import com.example.pokedex.common.model.PokeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ListService {
    @GET("pokemon")
    suspend fun getPokemonList(@Query("limit") limit: Int, @Query("offset") offset: Int): PokeResponse

    @GET("pokemon/{id}")
    suspend fun getPokemon(@Path("id") id: Int): PokeDTO
}
