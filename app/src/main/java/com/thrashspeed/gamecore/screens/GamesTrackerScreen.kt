package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.data.model.GameEntity
import com.thrashspeed.gamecore.data.model.GameStatus
import com.thrashspeed.gamecore.navigation.AppScreens
import com.thrashspeed.gamecore.screens.viewmodels.GamesTrackerViewModel
import com.thrashspeed.gamecore.utils.igdb.IgdbHelperMethods
import com.thrashspeed.gamecore.utils.igdb.IgdbImageSizes

@Composable
fun GamesTrackerScreen(topLevelNavController: NavController, navController: NavController, viewModel: GamesTrackerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val selectedTabIndex = viewModel.selectedTabIndex.value
    GamesTrackerBodyContent(topLevelNavController, navController, viewModel, selectedTabIndex)
}

@Composable
fun GamesTrackerBodyContent(topLevelNavController: NavController, navController: NavController, viewModel: GamesTrackerViewModel, initialTabIndex: Int) {
    var selectedTabIndex by remember { mutableIntStateOf(initialTabIndex) }
    val tabs = listOf(
        LocalContext.current.getString(R.string.exploreTabs_games), LocalContext.current.getString(
            R.string.gamesTrackerTabs_stats), LocalContext.current.getString(
            R.string.gamesTrackerTabs_lists))

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        viewModel.setSelectedTabIndex(selectedTabIndex)
                    }
                )
            }
        }

        Column (
        ) {
            when (selectedTabIndex) {
                0 -> GamesSaved(topLevelNavController = topLevelNavController, navController = navController, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GamesSaved(topLevelNavController: NavController, navController: NavController, viewModel: GamesTrackerViewModel) {
    val toPlayGames = viewModel.getGamesByStatus(GameStatus.TO_PLAY).observeAsState(initial = emptyList())
    val completedGames = viewModel.getGamesByStatus(GameStatus.COMPLETED).observeAsState(initial = emptyList())
    val playingGames = viewModel.getGamesByStatus(GameStatus.NOW_PLAYING).observeAsState(initial = emptyList())

    Column {
        GamesHorizontalList(title = "To Play", games = toPlayGames.value, topLevelNavController = topLevelNavController)
        GamesHorizontalList(title = "Completed", games = completedGames.value, topLevelNavController = topLevelNavController)
        GamesHorizontalList(title = "Playing", games = playingGames.value, topLevelNavController = topLevelNavController)
    }
}

// PONER MENÚ A ESTE ARRIBA DERECHA O ALGO A LA FOTO DEL JUEGO PARA BORRAR ETC
@Composable
fun GameEntityItem(game: GameEntity, topLevelNavController: NavController) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .padding(horizontal = 8.dp)
    ) {
        AsyncImage(
            model = if (game.coverImageUrl != null) IgdbHelperMethods.getImageUrl(game.coverImageUrl ?: "", IgdbImageSizes.SIZE_720P) else R.drawable.ic_launcher_background,
            contentDescription = game.name + " cover image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .align(Alignment.CenterHorizontally)
                .height(190.dp)
                .clickable {
                    topLevelNavController.navigate("${AppScreens.GameDetailsScreen.route}/${game.gameId}")
                }
        )
    }
}

@Composable
fun GamesHorizontalList(
    title: String,
    games: List<GameEntity>,
    topLevelNavController: NavController
) {
    Column {
        Text(
            text = title,
            modifier = Modifier.padding(8.dp),
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (games.isNotEmpty()) {
            LazyRow {
                items(items = games) { game ->
                    GameEntityItem(game = game, topLevelNavController)
                }
            }
        } else {
            Row (
                modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No games yet!",
                    color = Color.Gray,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Default.Castle, contentDescription = "Castle", tint = Color.Gray)
            }
        }
    }
}

