package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.screens.viewmodels.MyGamesViewModel

@Composable
fun MyGamesScreen(topLevelNavController: NavController, navController: NavController, viewModel: MyGamesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val selectedTabIndex = viewModel.selectedTabIndex.value
    MyGamesBodyContent(topLevelNavController, navController, viewModel, selectedTabIndex)
}

@Composable
fun MyGamesBodyContent(topLevelNavController: NavController, navController: NavController, viewModel: MyGamesViewModel, initialTabIndex: Int) {
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
            modifier = Modifier.verticalScroll(rememberScrollState()) // Enable vertical scrolling

        ) {
            when (selectedTabIndex) {
                0 -> GamesTrackerScreen(topLevelNavController = topLevelNavController, navController = navController)
                2 -> ListsScreen(topLevelNavController = topLevelNavController, navController = navController)
            }
        }
    }
}