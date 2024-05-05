package com.thrashspeed.gamecore.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.thrashspeed.gamecore.R
import com.thrashspeed.gamecore.data.model.GameItem
import com.thrashspeed.gamecore.navigation.AppScreens
import com.thrashspeed.gamecore.screens.viewmodels.ExploreViewModel
import com.thrashspeed.gamecore.utils.composables.LoadingIndicator
import com.thrashspeed.gamecore.utils.igdb.IgdbData
import com.thrashspeed.gamecore.utils.igdb.IgdbHelperMethods
import com.thrashspeed.gamecore.utils.igdb.IgdbImageSizes
import com.thrashspeed.gamecore.utils.igdb.IgdbSortOptions
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExploreScreen(topLevelNavController: NavController, navController: NavController, viewModel: ExploreViewModel = viewModel()) {
    val selectedTabIndex = viewModel.selectedTabIndex.value
    ExploreScreenBodyContent(topLevelNavController = topLevelNavController, navController = navController, viewModel = viewModel, initialTabIndex = selectedTabIndex)
}

@Composable
fun ExploreScreenBodyContent(topLevelNavController: NavController, navController: NavController, viewModel: ExploreViewModel, initialTabIndex: Int) {
    var selectedTabIndex by remember { mutableIntStateOf(initialTabIndex) }
    val horizontalListScrollState = rememberLazyListState()
    val verticalListScrollState = rememberLazyListState()

    val tabs = listOf(LocalContext.current.getString(R.string.exploreTabs_trending), LocalContext.current.getString(R.string.exploreTabs_catalogue))

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                0 -> GamesExploreContent(topLevelNavController = topLevelNavController, navController = navController, viewModel = viewModel, horizontalListScrollState, verticalListScrollState)
                1 -> GamesCatalogue(topLevelNavController = topLevelNavController, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GamesExploreContent(topLevelNavController: NavController, navController: NavController, viewModel: ExploreViewModel, horizontalListScrollState: LazyListState, verticalListScrollState: LazyListState) {
    val trendingGamesState by remember(viewModel) { viewModel.trendingGames }.collectAsState()
    val latestBestRatedGamesState by remember(viewModel) { viewModel.latestBestRatedGames }.collectAsState()
    val mostHypedGamesState by remember(viewModel) { viewModel.mostHypedGames }.collectAsState()

    if (trendingGamesState.isEmpty() || latestBestRatedGamesState.isEmpty() || mostHypedGamesState.isEmpty()) {
        LoadingIndicator()
    } else {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp, 8.dp)
            ) {
                Icon(imageVector = Icons.Default.WbSunny, contentDescription = "Trending icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = LocalContext.current.getString(R.string.explore_trendingGames),
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                )
            }
            GamesHorizontalList(games = trendingGamesState, topLevelNavController = topLevelNavController)
            Spacer(modifier = Modifier.height(8.dp))

            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp, 8.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "Latest hits icon", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = LocalContext.current.getString(R.string.explore_latestHits),
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                )
            }
            GamesHorizontalList(games = latestBestRatedGamesState, topLevelNavController = topLevelNavController)
            Spacer(modifier = Modifier.height(8.dp))

            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp, 8.dp)
            ) {
                Icon(imageVector = Icons.Default.HourglassTop, contentDescription = "Hyped icon", tint = MaterialTheme.colorScheme.tertiary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = LocalContext.current.getString(R.string.explore_mostHyped),
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                )
            }
            GamesHorizontalList(games = mostHypedGamesState, topLevelNavController = topLevelNavController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenresBottomSheet(viewModel: ExploreViewModel, genresToApply: SnapshotStateList<Int>, sortOption: MutableState<IgdbSortOptions>, isLoading: MutableState<Boolean>, onDismiss: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
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
                    coroutineScope.launch {
                        isLoading.value = true
                        viewModel.updateGamesInList(genresToApply, sortOption.value) { success ->
                            if (success) isLoading.value = false
                        }
                        modalBottomSheetState.hide()
                        onDismiss()
                    }
                }
            ) {
                Text(text = LocalContext.current.getString(R.string.explore_applyFilter))
            }
            GenresLabelsContainer(genresToApply)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenresLabelsContainer(genresToApply: SnapshotStateList<Int>) {
    FlowRow(
    ) {
        IgdbData.genreIdNamePairs.forEach { (genreId, genreName) ->
            var isSelected = genresToApply.contains(genreId)

            GenreLabel(
                genreName = genreName,
                isSelected = isSelected,
                onGenreSelected = {
                    isSelected = !isSelected
                    if (isSelected) {
                        genresToApply.add(genreId)
                    } else {
                        genresToApply.remove(genreId)
                    }
                }
            )
        }
    }
}

@Composable
fun GenreLabel(
    genreName: String,
    isSelected: Boolean,
    onGenreSelected: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary // Use primary color when selected
    } else {
        MaterialTheme.colorScheme.surface // Use surface color when not selected
    }

    Box(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onGenreSelected() }
            .background(color = backgroundColor)
    ) {
        Text(
            text = genreName,
            modifier = Modifier.padding(8.dp),
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary // Use onPrimary color when selected
            } else {
                MaterialTheme.colorScheme.onSurface // Use onSurface color when not selected
            }
        )
    }
}

@Composable
fun GamesCatalogue(
    topLevelNavController: NavController,
    viewModel: ExploreViewModel
) {
    val context = LocalContext.current
    val filteredGamesState by remember(viewModel) { viewModel.filteredGames }.collectAsState()

    var showSheet by remember { mutableStateOf(false) }
    var showDropdown by remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }
    val dropdownMenuTitle = remember { mutableStateOf(context.getString(R.string.explore_sortByMostPlayed)) }
    val genresList = remember { mutableStateListOf<Int>() }
    val sortOption = remember { mutableStateOf(IgdbSortOptions.MOST_PLAYED) }

    if (showSheet) {
        GenresBottomSheet(
            viewModel = viewModel,
            sortOption = sortOption,
            genresToApply = genresList,
            isLoading = isLoading
        ) {
            showSheet = false
        }
    }

    Column {
        Row (
            modifier = Modifier.padding(12.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = LocalContext.current.getString(R.string.explore_gamesCatalogue),
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showSheet = true }) {
                Icon(imageVector = Icons.Default.FilterList, contentDescription = "FilterListIcon")
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                contentPadding = PaddingValues(8.dp),
                onClick = { showDropdown = true }
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = dropdownMenuTitle.value)
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "SortByIcon")
                }

                if (showDropdown) {
                    SortByDropDownMenu(viewModel = viewModel, sortOption = sortOption, genresToApply = genresList, showDropdown = true, dropdownTitle = dropdownMenuTitle, isLoading = isLoading) {
                        showDropdown = false
                    }
                }
            }
        }

        if (isLoading.value) {
            LoadingIndicator()
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                itemsIndexed(filteredGamesState) { index, game ->
                    GameListItem(index = index, game = game) { gameClickedId ->
                        topLevelNavController.navigate("${AppScreens.GameDetailsScreen.route}/$gameClickedId")
                    }
                }
            }
        }
    }
}

@Composable
fun GameListItem(index: Int, game: GameItem, onItemClick: (Long) -> Unit) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(12.dp))
            .padding(8.dp)
            .clickable { onItemClick(game.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.width(42.dp),
            text = "#${index + 1}"
        )
        AsyncImage(
            model = if (game.cover != null) IgdbHelperMethods.getImageUrl(game.cover.image_id ?: "", IgdbImageSizes.COVER_BIG) else R.drawable.ic_launcher_background,
            contentDescription = game.name + " cover image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .height(80.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column (
        ) {
            Text(
                text = game.name
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                color = Color.Gray,
                text = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(game.first_release_date * 1000))
            )
        }
    }
}

// TODO
// GUARDAR OPCIÓN SELECCIONADA DEL DROPDOWN CUANDO SE VUELVE A LA PANTALLA TRAS NAVEGAR, nah...
@Composable
fun SortByDropDownMenu(viewModel: ExploreViewModel, genresToApply: SnapshotStateList<Int>, sortOption: MutableState<IgdbSortOptions>, showDropdown: Boolean, dropdownTitle: MutableState<String>, isLoading: MutableState<Boolean>, onDismiss: () -> Unit ) {
    val context = LocalContext.current

    DropdownMenu(
        expanded = showDropdown,
        onDismissRequest = { onDismiss() }
    ) {
        DropdownMenuItem(
            text = { Text(text = context.getString(R.string.explore_sortByBestRated)) },
            onClick = {
                sortOption.value = IgdbSortOptions.RATING
                dropdownTitle.value = context.getString(R.string.explore_sortByBestRated)

                isLoading.value = true
                viewModel.updateGamesInList(genresToApply, sortOption.value) { success ->
                    if (success) isLoading.value = false
                }

                onDismiss()
            }
        )
        DropdownMenuItem(
            text = { Text(text = context.getString(R.string.explore_sortByMostPlayed)) },
            onClick = {
                sortOption.value = IgdbSortOptions.MOST_PLAYED
                dropdownTitle.value = context.getString(R.string.explore_sortByMostPlayed)

                isLoading.value = true
                viewModel.updateGamesInList(genresToApply, sortOption.value) { success ->
                    if (success) isLoading.value = false
                }

                onDismiss()
            }
        )
    }
}

//@Composable
//fun PlatformsGrid(
//    platforms: List<PlatformItem>,
//    scrollState: LazyListState,
//    modifier: Modifier = Modifier
//) {
//    LazyColumn(modifier = modifier, state = scrollState) {
//        items(platforms.chunked(3)) { chunk ->
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                chunk.forEach { platform ->
//                    Column {
//                        AsyncImage(
//                            model = if (platform.platform_logo != null) IgdbHelperMethods.getImageUrl(platform.platform_logo.image_id ?: "", IgdbImageSizes.SCREENSHOT_MED) else R.drawable.ic_launcher_background,
//                            contentDescription = platform.name + " logo",
//                            contentScale = ContentScale.Fit,
//                            modifier = Modifier
//                                .clip(RoundedCornerShape(4.dp))
//                                .width(120.dp)
//                                .height(120.dp)
//                        )
//                        Text(
//                            textAlign = TextAlign.Center,
//                            text = platform.name
//                        )
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//    }
//}

@Composable
fun GamesHorizontalList(games: List<GameItem>, topLevelNavController: NavController) {
    LazyRow(
    ) {
        items(games) { gameItem ->
            GameBigListItem(gameItem = gameItem, topLevelNavController = topLevelNavController)
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun GameBigListItem(gameItem: GameItem, topLevelNavController: NavController) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .padding(horizontal = 8.dp)
    ) {
        AsyncImage(
            model = if (gameItem.cover != null) IgdbHelperMethods.getImageUrl(gameItem.cover.image_id ?: "", IgdbImageSizes.SIZE_720P) else R.drawable.ic_launcher_background,
            contentDescription = gameItem.name + " cover image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .align(Alignment.CenterHorizontally)
                .height(190.dp)
                .clickable {
                    topLevelNavController.navigate("${AppScreens.GameDetailsScreen.route}/${gameItem.id}")
                }
        )
    }
}