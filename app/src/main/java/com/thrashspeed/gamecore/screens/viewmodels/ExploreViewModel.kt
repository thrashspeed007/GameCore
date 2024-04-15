package com.thrashspeed.gamecore.screens.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrashspeed.gamecore.data.access.GamesAccess
import com.thrashspeed.gamecore.data.access.PlatformsAccess
import com.thrashspeed.gamecore.data.model.GameItem
import com.thrashspeed.gamecore.data.model.PlatformItem
import com.thrashspeed.gamecore.utils.igdb.IgdbSortOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ExploreViewModel : ViewModel() {
    private val _selectedTabIndex = mutableIntStateOf(0)
    val selectedTabIndex: State<Int> = _selectedTabIndex

    fun setSelectedTabIndex(index: Int) {
        _selectedTabIndex.intValue = index
    }

    private val gamesAccess = GamesAccess()
    private val platformsAccess = PlatformsAccess()

    // State to hold the list of popular games
    private val _filteredGames = MutableStateFlow<List<GameItem>>(emptyList())
    private val _trendingGames = MutableStateFlow<List<GameItem>>(emptyList())
    private val _filteredPlatforms = MutableStateFlow<List<PlatformItem>>(emptyList())
    val filteredGames = _filteredGames
    val trendingGames = _trendingGames
    val filteredPlatforms = _filteredPlatforms

    init {
        fetchFilteredGames()
        fetchTrendingGames()
        fetchFilteredPlatforms()
    }

    private fun fetchFilteredGames() {
        viewModelScope.launch {
            gamesAccess.getFamousGames { games ->
                _filteredGames.value = games
            }
        }
    }

    private fun fetchTrendingGames() {
        viewModelScope.launch {
            gamesAccess.getTrendingGames { games ->
                _trendingGames.value = games
            }
        }
    }

    private fun fetchFilteredPlatforms() {
        viewModelScope.launch {
            platformsAccess.getLatestPlatforms { platforms ->
                _filteredPlatforms.value = platforms
            }
        }
    }

    fun updateGamesInList(genres: List<Int>, sortOption: IgdbSortOptions, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            gamesAccess.getFilteredGames(genres, sortOption) { games ->
                _filteredGames.value = games
                callback.invoke(true)
            }
        }
    }
}