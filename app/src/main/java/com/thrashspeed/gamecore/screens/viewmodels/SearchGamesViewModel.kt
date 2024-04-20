package com.thrashspeed.gamecore.screens.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrashspeed.gamecore.data.access.GamesAccess
import com.thrashspeed.gamecore.data.model.GameItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SearchGamesViewModel: ViewModel() {
    private val gamesAccess = GamesAccess()
    private val _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText

    private val _gamesSearched = MutableStateFlow<List<GameItem>>(emptyList())
    val gamesSearched = _gamesSearched

    fun setSearchText(text: String) {
        _searchText.value = text
    }

    fun fetchGamesBySearch(search: String,  callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            gamesAccess.searchGames(search) { games ->
                _gamesSearched.value = games
                callback.invoke(games.isNotEmpty())
            }
        }
    }

}