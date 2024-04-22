package com.thrashspeed.gamecore.navigation

sealed class AppScreens(val route: String) {
    object HomeNavigation: AppScreens("home_screen")
    object ExploreScreen: AppScreens("explore_games_screen")
    object GamesTrackerScreen: AppScreens("games_tracker_screen")
    object GameDetailsScreen: AppScreens("game_deatils_screen")
    object SearchGamesScreen: AppScreens("search_games_screen")
}