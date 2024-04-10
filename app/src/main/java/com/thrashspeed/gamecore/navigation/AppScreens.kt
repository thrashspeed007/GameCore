package com.thrashspeed.gamecore.navigation

sealed class AppScreens(val route: String) {
    object ExploreScreen: AppScreens("explore_games_screen")
    object GamesTrackerScreen: AppScreens("games_tracker_screen")
    object GameDetailsScreen: AppScreens("game_deatils_screen")
}