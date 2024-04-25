package com.thrashspeed.gamecore.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.thrashspeed.gamecore.screens.viewmodels.ListsViewModel

@Composable
fun ListsScreen(topLevelNavController: NavController, navController: NavController, viewModel: ListsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    ListsBodyContent()
}

@Composable
fun ListsBodyContent() {

}