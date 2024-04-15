package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
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

    if (game == null) {
        LoadingIndicator()
    } else {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = game.name,
                modifier = Modifier.padding(vertical = 8.dp),
                fontSize = 24.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row {
                Column (
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(game.first_release_date * 1000)))
                    Text(
                        text = game.genres.joinToString { it.name },
                        fontSize = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = ((game.total_rating * 10.0).roundToInt() / 10.0 ).toString()
                    )

                }
                Spacer(modifier = Modifier.width(8.dp))
                AsyncImage(
                    model = if (game?.cover != null) IgdbHelperMethods.getImageUrl(game.cover.image_id ?: "", IgdbImageSizes.SIZE_720P) else R.drawable.ic_launcher_background,
                    contentDescription = game.name + " cover image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .width(140.dp)
                )
            }
        }
    }
}