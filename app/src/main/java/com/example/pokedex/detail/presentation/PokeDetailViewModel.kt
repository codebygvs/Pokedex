package com.example.pokedex.detail.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.pokedex.common.data.RetrofitClient
import com.example.pokedex.common.model.PokeDTO
import com.example.pokedex.detail.data.DetailService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class PokeDetailViewModel(
    private val detailService: DetailService,
    private val pokemonId: String
) : ViewModel() {

    private val _uiPokemonDetail = MutableStateFlow<PokeDTO?>(null)
    val uiPokemonDetail: StateFlow<PokeDTO?> = _uiPokemonDetail.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchPokemonDetail()
    }

    private fun fetchPokemonDetail() {
        val id = pokemonId.toIntOrNull()
        if (id != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    _isLoading.value = true
                    val pokemon = detailService.getPokemon(id)
                    _uiPokemonDetail.value = pokemon
                } catch (e: Exception) {
                    Log.d("PokeDetailViewModel", "Network Error :: ${e.message}")
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            Log.d("PokeDetailViewModel", "Invalid Pokemon ID")
            _isLoading.value = false
        }
    }


    companion object {
        fun provideFactory(pokemonId: String): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val detailService =
                        RetrofitClient.retrofitInstance.create(DetailService::class.java)
                    return PokeDetailViewModel(
                        detailService, pokemonId
                    ) as T
                }
            }
        }
    }
}

