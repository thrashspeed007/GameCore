package com.thrashspeed.gamecore.navigation

sealed class AppScreens(val route: String) {
    object AuthScreen: AppScreens("auth_screen")
    object HomeNavigation: AppScreens("home_screen")
    object ExploreScreen: AppScreens("explore_games_screen")
    object MyGamesScreen: AppScreens("my_games_screen")
    object ProfileScreen: AppScreens("profile_screen")
    object GameDetailsScreen: AppScreens("game_details_screen")
    object SearchGamesScreen: AppScreens("search_games_screen")
    object GamesTrackerScreen: AppScreens("games_tracker_screen")
    object StatsScreen: AppScreens("stats_screen")
    object ListsScreen: AppScreens("lists_screen")
    object AddGameToListScreen: AppScreens("add_game_to_list_screen")
    object ListContentScreen: AppScreens("list_content_screen")
    object SettingsScreen: AppScreens("settings_screen")
}