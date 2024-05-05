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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NextPlan
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Castle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.asLongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.data.model.GameEntity
import com.thrashspeed.gamecore.data.model.GameStatus
import com.thrashspeed.gamecore.navigation.AppScreens
import com.thrashspeed.gamecore.screens.viewmodels.GamesTrackerViewModel
import com.thrashspeed.gamecore.utils.composables.DeleteDialog
import com.thrashspeed.gamecore.utils.igdb.IgdbHelperMethods
import com.thrashspeed.gamecore.utils.igdb.IgdbImageSizes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GamesTrackerScreen(topLevelNavController: NavController, navController: NavController, viewModel: GamesTrackerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val toPlayGames = viewModel.getGamesByStatus(GameStatus.TO_PLAY).observeAsState(initial = emptyList())
    val completedGames = viewModel.getGamesByStatus(GameStatus.COMPLETED).observeAsState(initial = emptyList())
    val playingGames = viewModel.getGamesByStatus(GameStatus.NOW_PLAYING).observeAsState(initial = emptyList())

    Column (
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        GameSession(viewModel = viewModel)
        Divider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp), thickness = 2.dp)
        GamesHorizontalList(title = "To Play", icon = Icons.AutoMirrored.Filled.NextPlan, iconColor = MaterialTheme.colorScheme.secondary, games = toPlayGames.value, topLevelNavController = topLevelNavController, viewModel = viewModel)
        GamesHorizontalList(title = "Playing", icon = Icons.Default.VideogameAsset, iconColor = MaterialTheme.colorScheme.primary, games = playingGames.value, topLevelNavController = topLevelNavController, viewModel = viewModel)
        GamesHorizontalList(title = "Completed", icon = Icons.Default.Verified, iconColor = MaterialTheme.colorScheme.tertiary, games = completedGames.value, topLevelNavController = topLevelNavController, viewModel = viewModel)
    }
}

@Composable
fun GameSession(viewModel: GamesTrackerViewModel) {
    val context = LocalContext.current
    viewModel.checkGameSession(context)

    val gameInSessionId by viewModel.gameInSessionId.asLongState()
    val game by viewModel.getGameById(gameInSessionId).observeAsState()
    val sessionActive by viewModel.isSessionActive
    var currentSessionTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(game) {
        currentSessionTime = viewModel.getSessionActiveTime(game?.sessionStartedTempDate ?: 0)
    }

    Column (
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Row (
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Gamepad, contentDescription = "Game session icon", tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Game session",
                fontSize = 24.sp
            )
        }
        
        if (gameInSessionId == -1L) {
            Text(text = "No game in session! To add one, choose 'Create session' on a playing game options.", color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth())
        } else {
            Column (
            ) {
                Text(text = game?.name ?: "", fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Row (
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column (
                        modifier = Modifier.height(190.dp),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Column {
                            Text(text = "Total time:")
                            Spacer(modifier = Modifier.height(2.dp))
                            Row {
                                Icon(imageVector = Icons.Default.AccessTime, contentDescription = "Total time icon")
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(text = viewModel.formatDuration(game?.timePlayed ?: 0L))
                            }
                        }
                        Column {
                            Text(text = "Last time:")
                            Spacer(modifier = Modifier.height(2.dp))
                            Row {
                                Icon(imageVector = Icons.Default.Timelapse, contentDescription = "Last session time icon")
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(text = viewModel.formatDuration(game?.lastSessionTimePlayed ?: 0L, true))
                            }
                        }
                    }

                    AsyncImage(
                        model = if (game?.coverImageUrl != null) IgdbHelperMethods.getImageUrl(game?.coverImageUrl ?: "", IgdbImageSizes.SIZE_720P) else "",
                        contentDescription = game?.name + " cover image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .height(190.dp)
                    )

                    Column (
                        modifier = Modifier.height(190.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column {
                            Text(text = "Current time:")

                            if (sessionActive) {
                                LaunchedEffect(Unit) {
                                    while (true) {
                                        currentSessionTime += 1000
                                        delay(1000)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Row {
                                Icon(imageVector = Icons.Default.Timer, contentDescription = "Current time icon")
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(text = viewModel.formatDuration(currentSessionTime, true))
                            }
                        }
                        if (sessionActive) {
                            IconButton(
                                onClick = {
                                    viewModel.endSession(context, game!!, currentSessionTime)
                                    currentSessionTime = 0L
                                },
                                modifier = Modifier.size(64.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop game session button", modifier = Modifier.size(36.dp))
                            }
                        } else {
                            IconButton(onClick = { viewModel.startSession(context, game!!) }, modifier = Modifier.size(64.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White)) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play game session button", modifier = Modifier.size(36.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameEntityItem(game: GameEntity, topLevelNavController: NavController, viewModel: GamesTrackerViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showSheet) {
        ChangeGameStatusBottomSheet(
            game = game,
            viewModel = viewModel,
            onDismissBottomSheet = { showSheet = false }
        )
    }

    if (showDeleteDialog) {
        DeleteDialog(dialogTitleText = "Delete game", dialogContentText = "Are you sure you want to delete the game?") { confirmed ->
            showDeleteDialog = false
            if (confirmed) {
                viewModel.deleteGame(context, game)
            }
        }
    }

    if (showDetailsDialog) {
        GameDetailsDialog(viewModel = viewModel, game = game) {
            showDetailsDialog = false
        }
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
                    if (game.status == GameStatus.NOW_PLAYING) {
                        DropdownMenuItem(
                            text = {
                                Text(text = "Create session", color = Color(0xFF4CAF50))
                            },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.PlayCircle, contentDescription = "Create game session", tint = Color(0xFF4CAF50))
                            },
                            onClick = {
                                viewModel.addGameToSession(context, game.id)
                                expanded = false
                            }
                        )
                    }

                    DropdownMenuItem(
                        text = {
                            Text(text = "Details")
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Create, contentDescription = "Game details")
                        },
                        onClick = {
                            showDetailsDialog = true
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
                            showDeleteDialog = true
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
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = "List description icon", modifier = Modifier.size(24.dp), tint = iconColor)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                modifier = Modifier.padding(start = 8.dp),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailsDialog(viewModel: GamesTrackerViewModel, game: GameEntity, onDismiss: () -> Unit) {
    var firstDayOfPlayDate by remember { mutableLongStateOf(game.firstDayOfPlay) }
    var dayEndedDate by remember { mutableLongStateOf(game.dayOfCompletion) }
    var hours by remember { mutableLongStateOf(game.timePlayed / (1000 * 60 * 60)) }
    var minutes by remember { mutableLongStateOf(game.timePlayed % (1000 * 60 * 60) / (1000 * 60)) }


    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Game details") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "First day of play:", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(firstDayOfPlayDate)),
                        onValueChange = {
                            val parsedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(it)
                            firstDayOfPlayDate = parsedDate?.time ?: firstDayOfPlayDate
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Day of completion:", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(dayEndedDate)),
                        onValueChange = {
                            val parsedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(it)
                            dayEndedDate = parsedDate?.time ?: dayEndedDate
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Time played:", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Row (
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextField(
                            value = hours.toString(),
                            onValueChange = {
                                hours = it.take(3).toLongOrNull() ?: 0L
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(60.dp)
                        )
                        Text(text = "h")
                        TextField(
                            value = minutes.toString(),
                            onValueChange = {
                                if ((it.toLongOrNull() ?: 0) < 60) {
                                    minutes = it.take(2).toLongOrNull() ?: 0L
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(60.dp)
                        )
                        Text(text = "m")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {

                    onDismiss()
                }
            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Text(text = "Cancel")
            }
        },
        properties = DialogProperties(dismissOnClickOutside = true),
    )
}

