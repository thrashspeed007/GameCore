package com.thrashspeed.gamecore.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.navigation.AppScreens
import com.thrashspeed.gamecore.screens.viewmodels.MyGamesViewModel

@Composable
fun MyGamesScreen(topLevelNavController: NavController, navController: NavController, viewModel: MyGamesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val selectedTabIndex = viewModel.selectedTabIndex.value
    MyGamesBodyContent(topLevelNavController, navController, viewModel, selectedTabIndex)
}

@Composable
fun MyGamesBodyContent(topLevelNavController: NavController, navController: NavController, viewModel: MyGamesViewModel, initialTabIndex: Int) {
    val tabsNavController = rememberNavController()
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

        NavHost(
            navController = tabsNavController,
            startDestination = AppScreens.GamesTrackerScreen.route
        ) {
            composable(
                route = AppScreens.GamesTrackerScreen.route,
                enterTransition = { scaleIn(tween(200, 200)) },
                exitTransition = { scaleOut(tween(200)) }
            ) {
                GamesTrackerScreen(topLevelNavController = topLevelNavController, navController = navController)
            }
            composable(
                route = AppScreens.StatsScreen.route,
                enterTransition = { scaleIn(tween(200, 200)) },
                exitTransition = { scaleOut(tween(200)) }
            ) {
                // TODO
                // PONER PANTALLA DE STATS NO ESTA
                GamesTrackerScreen(topLevelNavController = topLevelNavController, navController = navController)
            }
            composable(
                route = AppScreens.ListsScreen.route,
                enterTransition = { scaleIn(tween(200, 200)) },
                exitTransition = { scaleOut(tween(200)) }
            ) {
                ListsScreen(topLevelNavController = topLevelNavController, navController = navController)
            }
        }

        when (selectedTabIndex) {
            0 -> {
                tabsNavController.navigate(AppScreens.GamesTrackerScreen.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            2 -> {
                tabsNavController.navigate(AppScreens.ListsScreen.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }
}