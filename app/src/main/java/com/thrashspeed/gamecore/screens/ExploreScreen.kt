package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.thrashspeed.gamecore.data.model.GameItem
import com.thrashspeed.gamecore.utils.igdb.IgdbHelperMethods
import com.thrashspeed.gamecore.utils.igdb.IgdbImageSizes

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
fun GameList(games: List<GameItem>) {
    LazyRow {
        items(games) { gameItem ->
            GameListItem(gameItem)
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun GameListItem(gameItem: GameItem) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .padding(horizontal = 8.dp)
    ) {
        AsyncImage(
            model = IgdbHelperMethods.getImageUrl(gameItem.cover.image_id, IgdbImageSizes.SIZE_720P),
            contentDescription = gameItem.name + " cover image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = gameItem.name ?: "",
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExploreScreenBodyContentPreview() {
    ExploreScreenBodyContent(navController = rememberNavController())
}