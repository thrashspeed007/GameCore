package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NextPlan
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import kotlinx.coroutines.launch

@Composable
fun GamesTrackerScreen(topLevelNavController: NavController, navController: NavController, viewModel: GamesTrackerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val toPlayGames = viewModel.getGamesByStatus(GameStatus.TO_PLAY).observeAsState(initial = emptyList())
    val completedGames = viewModel.getGamesByStatus(GameStatus.COMPLETED).observeAsState(initial = emptyList())
    val playingGames = viewModel.getGamesByStatus(GameStatus.NOW_PLAYING).observeAsState(initial = emptyList())

    Column (
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        GamesHorizontalList(title = "To Play", icon = Icons.AutoMirrored.Filled.NextPlan, iconColor = MaterialTheme.colorScheme.secondary, games = toPlayGames.value, topLevelNavController = topLevelNavController, viewModel = viewModel)
        GamesHorizontalList(title = "Playing", icon = Icons.Default.VideogameAsset, iconColor = MaterialTheme.colorScheme.primary, games = playingGames.value, topLevelNavController = topLevelNavController, viewModel = viewModel)
        GamesHorizontalList(title = "Completed", icon = Icons.Default.Verified, iconColor = MaterialTheme.colorScheme.tertiary, games = completedGames.value, topLevelNavController = topLevelNavController, viewModel = viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameEntityItem(game: GameEntity, topLevelNavController: NavController, viewModel: GamesTrackerViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }

    if (showSheet) {
        ChangeGameStatusBottomSheet(
            game = game,
            viewModel = viewModel,
            onDismissBottomSheet = { showSheet = false }
        )
    }

    Column(
        modifier = Modifier
            .width(160.dp)
            .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .height(190.dp)
                .clickable {
                    topLevelNavController.navigate("${AppScreens.GameDetailsScreen.route}/${game.gameId}")
                }
        ) {
            AsyncImage(
                model = if (game.coverImageUrl != null) IgdbHelperMethods.getImageUrl(game.coverImageUrl ?: "", IgdbImageSizes.SIZE_720P) else R.drawable.ic_launcher_background,
                contentDescription = game.name + " cover image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(32.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color.White,
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(text = "Details")
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Create, contentDescription = "Game details")
                        },
                        onClick = {

                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(text = "Change Status")
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Change status")
                        },
                        onClick = {
                            showSheet = true
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(text = "Delete", color = MaterialTheme.colorScheme.error)
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete game", tint = MaterialTheme.colorScheme.error)
                        },
                        onClick = {
                            viewModel.deleteGame(game)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  ChangeGameStatusBottomSheet(
    game: GameEntity,
    viewModel: GamesTrackerViewModel,
    onDismissBottomSheet: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val tagOptions = listOf(GameStatus.TO_PLAY, GameStatus.NOW_PLAYING, GameStatus.COMPLETED)
    var selectedStatus by remember { mutableStateOf(game.status) }
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = { onDismissBottomSheet() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.navigationBarsPadding(),
        ) {
            ToggleButtonGroup(
                options = tagOptions,
                selectedOption = game.status,
                onOptionSelected = {
                    selectedStatus = it
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    // TODO
                    // AÑADIR A FIRESTORE
                    viewModel.changeGameStatus(game = game, selectedStatus)
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                        onDismissBottomSheet()
                    }
                }
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Swap button")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "CHANGE STATE")
                }
            }
        }
    }
}

@Composable
fun GamesHorizontalList(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    games: List<GameEntity>,
    topLevelNavController: NavController,
    viewModel: GamesTrackerViewModel
) {
    Column {
        Row (
            modifier = Modifier.padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = "List description icon", modifier = Modifier.size(24.dp), tint = iconColor)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                modifier = Modifier.padding(8.dp),
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (games.isNotEmpty()) {
            LazyRow {
                items(items = games) { game ->
                    GameEntityItem(game = game, topLevelNavController = topLevelNavController, viewModel = viewModel)
                }
            }
        } else {
            Row (
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
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

