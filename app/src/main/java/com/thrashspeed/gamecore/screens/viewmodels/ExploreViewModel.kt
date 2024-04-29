package com.thrashspeed.gamecore.screens.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thrashspeed.gamecore.data.access.igdb.GamesAccess
import com.thrashspeed.gamecore.data.model.GameItem
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
//    private val platformsAccess = PlatformsAccess()

    // State to hold the list of popular games
    private val _filteredGames = MutableStateFlow<List<GameItem>>(emptyList())
    private val _trendingGames = MutableStateFlow<List<GameItem>>(emptyList())
    private val _latestBestRatedGames = MutableStateFlow<List<GameItem>>(emptyList())
    private val _mostHypedGames = MutableStateFlow<List<GameItem>>(emptyList())
//    private val _filteredPlatforms = MutableStateFlow<List<PlatformItem>>(emptyList())
    val filteredGames = _filteredGames
    val trendingGames = _trendingGames
    val latestBestRatedGames = _latestBestRatedGames
    val mostHypedGames = _mostHypedGames
//    val filteredPlatforms = _filteredPlatforms

    init {
        fetchFilteredGames()
        fetchTrendingGames()
        fetchLatestBestRatedGames()
        fetchMostHypedGames()
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

    private fun fetchLatestBestRatedGames() {
        viewModelScope.launch {
            gamesAccess.getLatestBestRatedGames { games ->
                _latestBestRatedGames.value = games
            }
        }
    }

    private fun fetchMostHypedGames() {
        viewModelScope.launch {
            gamesAccess.getMostHypedGames { games ->
                _mostHypedGames.value = games
            }
        }
    }

//    private fun fetchFilteredPlatforms() {
//        viewModelScope.launch {
//            platformsAccess.getLatestPlatforms { platforms ->
//                _filteredPlatforms.value = platforms
//            }
//        }
//    }

    fun updateGamesInList(genres: List<Int>, sortOption: IgdbSortOptions, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            gamesAccess.getFilteredGames(genres, sortOption) { games ->
                _filteredGames.value = games
                callback.invoke(true)
            }
        }
    }
}