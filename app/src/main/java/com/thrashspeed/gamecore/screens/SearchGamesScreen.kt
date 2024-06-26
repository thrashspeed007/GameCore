package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.thrashspeed.gamecore.navigation.AppScreens
import com.thrashspeed.gamecore.screens.viewmodels.SearchGamesViewModel
import com.thrashspeed.gamecore.utils.composables.LoadingIndicator

@Composable
fun SearchGamesScreen(topLevelNavController: NavController, navController: NavController, viewModel: SearchGamesViewModel = viewModel()) {
    SearchGamesScreenBodyContent(topLevelNavController = topLevelNavController, navController = navController, viewModel = viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchGamesScreenBodyContent(topLevelNavController: NavController, navController: NavController, viewModel: SearchGamesViewModel) {
    val searchText = viewModel.searchText.value
    var searchTextState by remember { mutableStateOf(searchText) }
    val searchedGamesState by remember(viewModel) { viewModel.gamesSearched }.collectAsState()
    var loading by remember { mutableStateOf(false) }
    var noResultsMessage by remember { mutableStateOf("") }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxSize()
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current

        SearchBar(
            modifier = Modifier.padding(bottom = 4.dp),
            query = searchText,
            onQueryChange = {
                searchTextState = it
                viewModel.setSearchText(it)
            },
            onSearch = {
                if (searchText.isNotEmpty()) {
                    loading = true
                    keyboardController?.hide()
                    viewModel.fetchGamesBySearch(searchText) { result ->
                        loading = false
                        noResultsMessage = if (!result) "No results for \"$searchText\"" else ""
                    }
                }
            },
            active = false,
            onActiveChange = {},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search, contentDescription = "Search icon"
                )
            },
            trailingIcon = {
                if (searchTextState.isNotEmpty()) {
                    IconButton(onClick = { searchTextState = ""; viewModel.setSearchText("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear icon")
                    }
                }
            },
            placeholder = {
                Text(text = "Search games...")
            }
        ) {

        }

        if (loading) {
            LoadingIndicator()
        } else {
            if (noResultsMessage.isNotEmpty()) {
                Text(
                    fontSize = 24.sp,
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center,
                    text = noResultsMessage
                )
            } else {
                LazyColumn(
                ) {
                    itemsIndexed(searchedGamesState) { index, game ->
                        GameListItem(index = index, game = game) { gameClickedId ->
                            topLevelNavController.navigate("${AppScreens.GameDetailsScreen.route}/$gameClickedId")
                        }
                    }
                }
            }
        }
    }
}
