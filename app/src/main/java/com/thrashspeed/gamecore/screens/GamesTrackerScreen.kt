package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.screens.viewmodels.GamesTrackerViewModel

@Composable
fun GamesTrackerScreen(navController: NavController, viewModel: GamesTrackerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val selectedTabIndex = viewModel.selectedTabIndex.value
    GamesTrackerBodyContent(navController, viewModel, selectedTabIndex)
}

@Composable
fun GamesTrackerBodyContent(navController: NavController, viewModel: GamesTrackerViewModel, initialTabIndex: Int) {
    var selectedTabIndex by remember { mutableIntStateOf(initialTabIndex) }
    val tabs = listOf(
        LocalContext.current.getString(R.string.exploreTabs_games), LocalContext.current.getString(
            R.string.gamesTrackerTabs_stats), LocalContext.current.getString(
            R.string.gamesTrackerTabs_lists))

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        viewModel.setSelectedTabIndex(selectedTabIndex)
                    }
                )
            }
        }

        Column (
        ) {
            when (selectedTabIndex) {
                0 -> GamesSaved(navController = navController, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GamesSaved(navController: NavController, viewModel: GamesTrackerViewModel) {
    val gamesState = viewModel.allGames.observeAsState(initial = emptyList())

    LazyColumn {
        items(items = gamesState.value) { game ->
            Text(text = game.title)
        }
    }
}
