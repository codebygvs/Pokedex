package com.example.pokedex.list.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.pokedex.common.data.RetrofitClient
import com.example.pokedex.common.model.PokeDTO
import com.example.pokedex.list.data.ListService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PokeListViewModel(
    private val listService: ListService
) : ViewModel() {

    private val _uiPokemonList = MutableStateFlow<List<PokeDTO>>(emptyList())
    val uiPokemonList: StateFlow<List<PokeDTO>> = _uiPokemonList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchPokemonList()
    }

    private fun fetchPokemonList() {

        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                //Obtém a lista principal dos pokemons
                val simplePokes = listService.getPokemonList(200, 0).resultsPoke

                //Busca detalhes de pokemon em paralelo usando coroutines
                val pokeDTOList = simplePokes.map { simplePokemon ->
                    async {
                        listService.getPokemon(simplePokemon.id)
                    }
                }.awaitAll() //Aguarda todas as chamadas serem concluidas

                pokeDTOList.sortedBy { it.id }
            }.onSuccess { sortedList ->
                withContext(Dispatchers.Main) {
                    _uiPokemonList.value = sortedList
                    _isLoading.value = false
                }
            }.onFailure { e->
                Log.d("PokeListViewModel", "Network Error: ${e.message}")
                withContext(Dispatchers.Main) {_isLoading.value = false}
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val listService = RetrofitClient.retrofitInstance.create(ListService::class.java)
                return PokeListViewModel(
                    listService
                ) as T
            }
        }
    }
}
