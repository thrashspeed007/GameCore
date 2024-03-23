package com.thrashspeed.gamecore.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Games
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.thrashspeed.gamecore.R

data class NavItem (
    val labelResId: Int,
    val icon: ImageVector,
    val route: String
) {
    @Composable
    fun getLabel(): String {
        return LocalContext.current.getString(labelResId)
    }
}

val listOfNavItems : List<NavItem> = listOf(
    NavItem(
        R.string.bottonNavBar_explore,
        Icons.Default.Explore,
        AppScreens.ExploreScreen.route
    ),
    NavItem(
        R.string.bottonNavBar_myGames,
        Icons.Default.Games,
        AppScreens.GamesTrackerScreen.route
    )
)