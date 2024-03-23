package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.thrashspeed.gamecore.navigation.AppScreens

@Composable
fun ExploreScreen(navController: NavController) {
    ExploreScreenBodyContent(navController = navController)
}

@Composable
fun ExploreScreenBodyContent(navController: NavController) {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("GameCore")
        Button(onClick = {
            navController.navigate(AppScreens.GamesTrackerScreen.route)
        }) {
            Text("Navegar")
        }
    }
}