package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.data.model.GameItem
import com.thrashspeed.gamecore.utils.igdb.IgdbHelperMethods
import com.thrashspeed.gamecore.utils.igdb.IgdbImageSizes

@Composable
fun ExploreScreen(navController: NavController) {
    ExploreScreenBodyContent(navController = navController)
}

@Composable
fun ExploreScreenBodyContent(navController: NavController, viewModel: ExploreViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    SliderWithTabs(viewModel)
}

@Composable
fun SliderWithTabs(viewModel: ExploreViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(LocalContext.current.getString(R.string.exploreTabs_games), LocalContext.current.getString(R.string.exploreTabs_platforms))

//    val scrollStates = rememberSaveable { mutableStateOf(listOf(rememberLazyListState(), rememberLazyListState())) }

    Column(
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
                    onClick = { selectedTabIndex = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content based on the selected tab index
        when (selectedTabIndex) {
            0 -> GamesExploreContent(viewModel)
            1 -> Text("Content for platforms")
        }
    }
}


@Composable
fun GamesExploreContent(viewModel: ExploreViewModel) {
    val gamesState by remember(viewModel) { viewModel.popularGames }.collectAsState()

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = LocalContext.current.getString(R.string.explore_famousGames),
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
            modifier = Modifier.padding(8.dp)
        )
        GamesHorizontalList(games = gamesState)
    }
}

@Composable
fun GamesHorizontalList(games: List<GameItem>) {
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