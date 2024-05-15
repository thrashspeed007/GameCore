package com.thrashspeed.gamecore.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.data.model.GameDetailed
import com.thrashspeed.gamecore.data.model.GameEntity
import com.thrashspeed.gamecore.data.model.GameStatus
import com.thrashspeed.gamecore.firebase.firestore.FirestoreRepository
import com.thrashspeed.gamecore.navigation.AppScreens
import com.thrashspeed.gamecore.screens.viewmodels.GameDetailsViewModel
import com.thrashspeed.gamecore.screens.viewmodels.GameDetailsViewModelFactory
import com.thrashspeed.gamecore.screens.viewmodels.Result
import com.thrashspeed.gamecore.utils.composables.LoadingIndicator
import com.thrashspeed.gamecore.utils.igdb.IgdbHelperMethods
import com.thrashspeed.gamecore.utils.igdb.IgdbImageSizes
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun GameDetailsScreen(navController: NavController, gameId: Int) {
    val viewModel: GameDetailsViewModel = viewModel(
        factory = GameDetailsViewModelFactory(gameId)
    )

    GameDetailsScreenBodyContent(topLevelNavController = navController, viewModel = viewModel)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GameDetailsScreenBodyContent(
    topLevelNavController: NavController,
    viewModel: GameDetailsViewModel
) {
    val gameDetailsState by viewModel.gameDetails.collectAsState()

    val game = gameDetailsState.firstOrNull()
    var showSheet by remember { mutableStateOf(false) }

    if (showSheet) {
        AddToTagBottomSheet(
            game = game,
            viewModel = viewModel,
            topLevelNavController = topLevelNavController,
            onDismissBottomSheet = { showSheet = false }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showSheet = true }) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("ADD")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Add, contentDescription = "ADD")
                }
            }
        }
    ) {
        if (game == null) {
            LoadingIndicator()
        } else {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)) {
                GameDetailsHeader(navController = topLevelNavController, gameName = game.name)
                GameDetailsContent(game = game)
            }
        }
    }
}


@Composable
fun GameDetailsHeader(navController: NavController, gameName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "GoBackIcon")
        }

        Text(
            text = gameName,
            modifier = Modifier.padding(end = 16.dp),
            fontSize = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun GameDetailsContent(game: GameDetailed) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row (
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Game release date")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(game.first_release_date * 1000)))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Category, contentDescription = "Game genres")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(game.genres.joinToString { it.name }, fontSize = 16.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.StarRate, contentDescription = "Game rating")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = ((game.total_rating * 10.0).roundToInt() / 10.0).toString())
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = if (game.cover != null) IgdbHelperMethods.getImageUrl(game.cover.image_id ?: "", IgdbImageSizes.SIZE_720P) else R.drawable.ic_launcher_background,
                contentDescription = "${game.name} cover image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .width(180.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column (
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
//            Row (
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(imageVector = Icons.Default.VideogameAsset, contentDescription = "Game companies")
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(text = game.involved_companies.joinToString(", "))
//            }
            Text(text = "Summary", fontSize = 20.sp )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = game.summary ?: "", maxLines = 10)

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow (
            ) {
                items(game.screenshots) { screenshot ->
                    AsyncImage(
                        model = IgdbHelperMethods.getImageUrl(screenshot.image_id, IgdbImageSizes.SIZE_720P),
                        contentDescription = "${game.name} screenshot",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .width(320.dp)
                            .height(180.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (game.storyline !== null) {
                Text(text = "Storyline", fontSize = 20.sp )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = game.storyline ?: "", maxLines = 10)
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToTagBottomSheet(
    game: GameDetailed?,
    viewModel: GameDetailsViewModel,
    topLevelNavController: NavController,
    onDismissBottomSheet: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val tagOptions = listOf(GameStatus.TO_PLAY, GameStatus.NOW_PLAYING, GameStatus.COMPLETED)
    var selectedStatus by remember { mutableStateOf(GameStatus.TO_PLAY) }
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
                selectedOption = GameStatus.TO_PLAY,
                onOptionSelected = {
                    selectedStatus = it
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val gameEntity = GameEntity (
                        id = game!!.id,
                        name = game.name,
                        releaseDate = game.first_release_date,
                        genres = game.genres
                            .filter { it.name.isNotBlank() }.joinToString(",") { it.name },
                        coverImageUrl = game.cover.image_id,
                        status = selectedStatus
                    )

                    coroutineScope.launch {
                        val localInsertionDeferred = async { viewModel.insertGame(gameEntity) }

                        val localInsertionResult = localInsertionDeferred.await()

                        if (localInsertionResult is Result.Error) {
                            Toast.makeText(context, "Error: ${localInsertionResult.exception}", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        modalBottomSheetState.hide()
                        onDismissBottomSheet()

                        if (localInsertionResult is Result.Success) {
                            launch {
                                FirestoreRepository.insertGame(gameEntity) { success ->
                                    if (!success) {
                                        Toast.makeText(context, "Failed to add the game to the cloud!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Ok button")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "ADD STATE")
                }
            }
            Divider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp))
            Button(
                onClick = {
//                    coroutineScope.launch {
//                        modalBottomSheetState.hide()
//                        onDismissBottomSheet()
//                    }
                    onDismissBottomSheet()
                    val gameItem = viewModel.gameDetails.value.firstOrNull()
                    topLevelNavController.navigate("${AppScreens.AddGameToListScreen.route}/${gameItem?.id}/${gameItem?.name}/${gameItem?.cover?.image_id}/${gameItem?.first_release_date}")
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = "Add to lists button")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "ADD TO LISTS")
                }
            }
        }
    }
}

@Composable
fun ToggleButtonGroup(
    options: List<GameStatus>,
    selectedOption: GameStatus?,
    onOptionSelected: (GameStatus) -> Unit
) {
    var selected by remember { mutableStateOf(selectedOption) }

    Row(
        modifier = Modifier.padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        options.forEachIndexed { index, option ->
            Button(
                onClick = {
                    selected = option
                    onOptionSelected(option)
                },
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (option == selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    contentColor = if (option == selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Text(text = option.displayName)
            }

            if (index < options.size - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
