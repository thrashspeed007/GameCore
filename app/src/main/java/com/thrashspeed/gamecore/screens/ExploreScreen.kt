package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.data.model.Game

@Composable
fun ExploreScreen(navController: NavController) {
    ExploreScreenBodyContent(navController = navController)
}

@Composable
fun ExploreScreenBodyContent(navController: NavController, viewModel: ExploreViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val popularGamesState = viewModel.popularGames.collectAsState()

    GameList(games = popularGamesState.value)
}

@Composable
fun GameList(games: List<Game>) {
    LazyColumn {
        items(games) { game ->
            GameListItem(game = game)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun GameListItem(game: Game) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background), // Placeholder image
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = game.name ?: "", color = Color.Black)
            // Display other game details as needed
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExploreScreenBodyContentPreview() {
    ExploreScreenBodyContent(navController = rememberNavController())
}