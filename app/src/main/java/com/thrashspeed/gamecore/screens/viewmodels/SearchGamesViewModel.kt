package com.thrashspeed.gamecore.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrashspeed.gamecore.data.access.GamesAccess
import com.thrashspeed.gamecore.data.model.GameItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SearchGamesViewModel: ViewModel() {
    private val gamesAccess = GamesAccess()

    private val _gamesSearched = MutableStateFlow<List<GameItem>>(emptyList())
    val gamesSearched = _gamesSearched

    init {
        fetchGamesBySearch(search = "")
    }

    fun fetchGamesBySearch(search: String) {
        viewModelScope.launch {
            gamesAccess.searchGames(search) { games ->
                _gamesSearched.value = games
            }
        }
    }

}