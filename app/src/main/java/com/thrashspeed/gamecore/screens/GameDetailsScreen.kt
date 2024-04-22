package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.data.model.GameDetailed
import com.thrashspeed.gamecore.screens.viewmodels.GameDetailsViewModel
import com.thrashspeed.gamecore.screens.viewmodels.GameDetailsViewModelFactory
import com.thrashspeed.gamecore.utils.composables.LoadingIndicator
import com.thrashspeed.gamecore.utils.igdb.IgdbHelperMethods
import com.thrashspeed.gamecore.utils.igdb.IgdbImageSizes
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

    GameDetailsScreenBodyContent(navController = navController, gameId = gameId, viewModel = viewModel)
}

@Composable
fun GameDetailsScreenBodyContent(navController: NavController, gameId: Int, viewModel: GameDetailsViewModel) {
    val gameDetailsState = remember(viewModel) {
        viewModel.gameDetails
    }.collectAsState()

    val game: GameDetailed? = gameDetailsState.value.firstOrNull()
    var showSheet by remember { mutableStateOf(false) }

    if (showSheet) {
        AddToTagBottomSheet (
            onDismissBottomSheet = {
                showSheet = false
            }
        )
    }

    if (game == null) {
        LoadingIndicator()
    } else {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ) {
            Row (
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.padding(end = 8.dp),
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "GoBackIcon")
                }

                Text(
                    text = game.name,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 16.dp),
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row (
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Column (
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Date: " + SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(game.first_release_date * 1000)))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = game.genres.joinToString { it.name },
                        fontSize = 16.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Rating: " + ((game.total_rating * 10.0).roundToInt() / 10.0 ).toString()
                    )
                    Button(
                        onClick = { showSheet = true }
                    ) {
                        Text(text = "Add")
                    }

                }
                Spacer(modifier = Modifier.width(8.dp))
                AsyncImage(
                    model = if (game?.cover != null) IgdbHelperMethods.getImageUrl(game.cover.image_id ?: "", IgdbImageSizes.SIZE_720P) else R.drawable.ic_launcher_background,
                    contentDescription = game.name + " cover image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .width(180.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToTagBottomSheet(
    onDismissBottomSheet: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val tagOptions = listOf("To Play", "Playing", "Completed")
    var selectedTag by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = { onDismissBottomSheet() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column (
            modifier = Modifier.navigationBarsPadding(),
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                onClick = {
                    // TODO
                    // AÑADIR A LAS TABLAS ROOM Y FIRESTORE
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                        onDismissBottomSheet()
                    }
                }
            ) {
                Text(text = "Add")
            }
            ToggleButtonGroup(
                options = tagOptions,
                selectedOption = tagOptions.first(),
                onOptionSelected = {
                    selectedTag = it
                }
            )
        }
    }
}

@Composable
fun ToggleButtonGroup(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
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
                Text(text = option)
            }

            if (index < options.size - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}


