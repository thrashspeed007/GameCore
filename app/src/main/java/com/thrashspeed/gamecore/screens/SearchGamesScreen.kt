package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.thrashspeed.gamecore.navigation.AppScreens
import com.thrashspeed.gamecore.screens.viewmodels.SearchGamesViewModel

@Composable
fun SearchGamesScreen(navController: NavController, viewModel: SearchGamesViewModel = viewModel()) {
    SearchGamesScreenBodyContent(navController = navController, viewModel = viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchGamesScreenBodyContent(navController: NavController, viewModel: SearchGamesViewModel) {
    var searchText by remember { mutableStateOf("") }
    val searchedGamesState by remember(viewModel) { viewModel.gamesSearched }.collectAsState()
    val verticalListScrollState = rememberLazyListState()

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxSize()
    ) {
        SearchBar(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = {
                viewModel.fetchGamesBySearch(searchText)
            },
            active = false,
            onActiveChange = {}
        ) {

        }

        LazyColumn(
            state = verticalListScrollState
        ) {
            itemsIndexed(searchedGamesState) { index, game ->
                GameListItem(index = index, game = game) { gameClickedId ->
                    navController.navigate("${AppScreens.GameDetailsScreen.route}/$gameClickedId")
                }
            }
        }
    }
}
