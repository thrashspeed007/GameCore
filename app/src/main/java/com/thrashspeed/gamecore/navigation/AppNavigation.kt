package com.thrashspeed.gamecore.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.data.model.GameCover
import com.thrashspeed.gamecore.data.model.GameItem
import com.thrashspeed.gamecore.screens.AddGameToListScreen
import com.thrashspeed.gamecore.screens.AuthScreen
import com.thrashspeed.gamecore.screens.ExploreScreen
import com.thrashspeed.gamecore.screens.GameDetailsScreen
import com.thrashspeed.gamecore.screens.ListContentScreen
import com.thrashspeed.gamecore.screens.MyGamesScreen
import com.thrashspeed.gamecore.screens.ProfileScreen
import com.thrashspeed.gamecore.screens.SearchGamesScreen
import com.thrashspeed.gamecore.screens.SettingsScreen

@Composable
fun AppNavigation(darkThemeCallback: (Boolean) -> Unit) {
    val topLevelNavController = rememberNavController()
    val homeNavController = rememberNavController()

    var isDarkTheme by remember { mutableStateOf(false) }
    val onThemeChanged: (Boolean) -> Unit = {
        isDarkTheme = it
        darkThemeCallback.invoke(it)
    }

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        NavHost(
            navController = topLevelNavController,
            startDestination = AppScreens.HomeNavigation.route
        ) {
            composable(
                route = AppScreens.AuthScreen.route,
                enterTransition = { slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) },
                exitTransition = { slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) }
            ) {
                AuthScreen(authCallback = {topLevelNavController.popBackStack()})
            }
            composable(
                route = AppScreens.HomeNavigation.route,
                enterTransition = { scaleIn(tween(200, 200)) },
                exitTransition = { scaleOut(tween(200)) }
            ) {
                HomeNavigation(
                    topLevelNavController = topLevelNavController,
                    navController = homeNavController
                )
            }
            composable(
                route ="${AppScreens.GameDetailsScreen.route}/{gameId}",
                enterTransition = { slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) },
                exitTransition = { slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) }
            ) {
                val gameId = it.arguments?.getString("gameId")

                GameDetailsScreen(navController = topLevelNavController, gameId = gameId?.toInt() ?: -1)
            }
            composable(
                route ="${AppScreens.AddGameToListScreen.route}/{gameId}/{name}/{cover}/{firstReleaseDate}",
                enterTransition = { slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) },
                exitTransition = { slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) }
            ) {
                val gameId = it.arguments?.getString("gameId")
                val name = it.arguments?.getString("name")
                val cover = it.arguments?.getString("cover")
                val firstReleaseDate = it.arguments?.getString("firstReleaseDate")

                val gameItem = GameItem(
                    id = gameId?.toLong() ?: -1,
                    name = name.toString(),
                    cover = GameCover(
                        id = -1,
                        image_id = cover.toString()
                    ),
                    first_release_date = firstReleaseDate?.toLong() ?: -1
                )

                AddGameToListScreen(topLevelNavController = topLevelNavController, game = gameItem)
            }
            composable(
                route = "${AppScreens.ListContentScreen.route}/{listId}",
                enterTransition = { slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) },
                exitTransition = { slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) }
            ) {
                val listId = it.arguments?.getString("listId")

                ListContentScreen(topLevelNavController = topLevelNavController, listId = listId?.toLong() ?: -1)
            }
            composable(
                route = AppScreens.SettingsScreen.route,
                enterTransition = { slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) },
                exitTransition = { slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) }
            ) {
                SettingsScreen(topLevelNavController = topLevelNavController, isDarkTheme = isDarkTheme, onThemeChanged = onThemeChanged)
            }
        }
    }
}

@Composable
fun HomeNavigation(topLevelNavController: NavHostController, navController: NavHostController) {
    Scaffold (
        topBar = { TopBar(topLevelNavController)},
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        NavHost (
            navController = navController,
            startDestination = AppScreens.MyGamesScreen.route,
            modifier = Modifier
                .padding(paddingValues)
        ) {
            composable(
                route = AppScreens.ExploreScreen.route,
                enterTransition = { scaleIn(tween(200, 200)) },
                exitTransition = { scaleOut(tween(200)) }
            ) {
                ExploreScreen(topLevelNavController, navController)
            }
            composable(
                route = AppScreens.MyGamesScreen.route,
                enterTransition = { scaleIn(tween(200, 200)) },
                exitTransition = { scaleOut(tween(200)) }
            ) {
                MyGamesScreen(topLevelNavController, navController)
            }
            composable(
                route = AppScreens.SearchGamesScreen.route,
                enterTransition = { scaleIn(tween(200, 200)) },
                exitTransition = { scaleOut(tween(200)) }
            ) {
                SearchGamesScreen(topLevelNavController, navController)
            }
            composable(
                route = AppScreens.ProfileScreen.route,
                enterTransition = { scaleIn(tween(200, 200)) },
                exitTransition = { scaleOut(tween(200)) }
            ) {
                ProfileScreen(topLevelNavController = topLevelNavController, navController = navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(topLevelNavController: NavController) {
    TopAppBar(
        title = {
            Text(LocalContext.current.getString(R.string.app_name) )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        actions = {
            IconButton(onClick = { topLevelNavController.navigate(AppScreens.SettingsScreen.route) }) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    )
}

@Composable
fun BottomBar(navController: NavController) {
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
                            restoreState = true
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