package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun GameDetailsScreen(navController: NavController, gameId: Int, viewModel: GameDetailsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    GameDetailsScreenBodyContent(navController = navController, gameId = gameId, viewModel = viewModel)
}

@Composable
fun GameDetailsScreenBodyContent(navController: NavController, gameId: Int, viewModel: GameDetailsViewModel) {
    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Juego con id: $gameId")
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Volver")
        }
    }
}