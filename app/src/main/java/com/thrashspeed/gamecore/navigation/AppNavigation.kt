package com.thrashspeed.gamecore.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thrashspeed.gamecore.screens.ExploreScreen
import com.thrashspeed.gamecore.screens.GamesTrackerScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold (
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                listOfNavItems.forEach { navItem ->
                    NavigationBarItem(
                        selected = currentRoute == navItem.route,
                        onClick = {
                            if (currentRoute != navItem.route) {
                                navController.navigate(navItem.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = {
                           Icon(
                               imageVector = navItem.icon,
                               contentDescription = null
                           )
                        },
                        label = {
                            Text(navItem.getLabel())
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost (
            navController = navController,
            startDestination = AppScreens.ExploreScreen.route,
            modifier = Modifier
                .padding(paddingValues)
        ) {
            composable(
                route = AppScreens.ExploreScreen.route,
                enterTransition = { scaleIn(tween(300)) },
                exitTransition = { scaleOut(tween(300)) }
            ) {
                ExploreScreen(navController)
            }
            composable(
                route = AppScreens.GamesTrackerScreen.route,
                enterTransition = { scaleIn(tween(300)) },
                exitTransition = { scaleOut(tween(300)) }
            ) {
                GamesTrackerScreen(navController)
            }
        }
    }
}