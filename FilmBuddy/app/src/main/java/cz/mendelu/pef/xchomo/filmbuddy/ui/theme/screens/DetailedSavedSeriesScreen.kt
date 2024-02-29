package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import cz.mendelu.pef.xchomo.filmbuddy.MediaItem
import cz.mendelu.pef.xchomo.filmbuddy.R
import cz.mendelu.pef.xchomo.filmbuddy.Season
import cz.mendelu.pef.xchomo.filmbuddy.SeasonsList
import cz.mendelu.pef.xchomo.filmbuddy.navigation.INavigationRouter
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.elements.BackArrowScreen
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.elements.BottomNavigationScreen

import kotlinx.coroutines.flow.combine
import org.koin.androidx.compose.getViewModel

@Composable
fun DetailedSavedSeriesScreen(
    navigation: INavigationRouter,
    viewModel: FilmScreenViewModel = getViewModel(),
) {
    val series = combine(
        viewModel.movieIdFlow,
        viewModel.movieTitleFlow,
        viewModel.movieOverviewFlow,
        viewModel.moviePosterPathFlow,
        viewModel.movieTypeFlow,
        viewModel.movieSeasonsFlow
    ) { values ->
        MediaItem(
            values[0] as Int,
            values[1] as String,
            values[2] as String,
            isChecked = false,
            values[3] as String,
            values[4] as Boolean,
            values[5] as SeasonsList
        )
    }.collectAsState(initial = MediaItem(0, "", "", false, "", true, SeasonsList(emptyList())))
    val seasons: List<Season>? = series.value.seasonsList?.seasons

    var isCheckedF by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = series.value.id) {
        viewModel.isInCollectionChecked(series.value.id.toString()) { isInCollection ->
            isCheckedF = isInCollection
        }
    }

    BackArrowScreen(
        topBarTitle = stringResource(R.string.app_name),
        onBackClick = { navigation.returnBack() },
        drawFullScreenContent = true,
        showCheckbox = true,
        isChecked = isCheckedF,
        onDeleteClick = {
            viewModel.deleteFromFirestore(series.value.id.toString())
            navigation.navigateToSeriesScreen()
        },
        onCheckedChange = { isChecked ->
            viewModel.setCheckedInFirestore(series.value.id.toString())
            isCheckedF = isChecked
        },

    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            BoxWithConstraints {
                val screenHeight = constraints.maxHeight.toFloat()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeight.dp - 56.dp)
                        .padding(bottom = 76.dp)// Subtract the height of the bottom navigation
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f) // Adjust the aspect ratio as desired
                            ) {
                                Image(
                                    painter = rememberImagePainter(data = "https://image.tmdb.org/t/p/w1280${series.value.poster_path}"),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = series.value.title,
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = series.value.overview,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            if (!series.value.mediaType && series.value.seasonsList != null) {
                                Text(
                                    text = stringResource(R.string.seasons),
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
                                )

                                Column {
                                    seasons!!.forEach { season ->
                                        SeasonCard(season = season, viewModel = viewModel, series = series.value)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    BottomNavigationScreen(navigation = navigation, currentScreen = "Series")
}

@Composable
fun SeasonCard(
    season: Season,
    viewModel: FilmScreenViewModel,
    series: MediaItem? = null
) {
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable(onClick = {
                isExpanded = !isExpanded
            })
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${stringResource(R.string.season)} ${season.season_number}",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(end = 16.dp)
            )

            Text(
                text = "${stringResource(R.string.episodes)}: ${season.episode_count}",
                style = MaterialTheme.typography.bodySmall
            )

            if (isExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    for (episodeIndex in 0 until season.episode_count) {


                        var isCheckedF by remember { mutableStateOf(false) }

                        LaunchedEffect(key1 = episodeIndex) {
                            val isChecked = viewModel.isEpisodeChecked(series?.id.toString(), episodeIndex + 1, season.season_number)
                            isCheckedF = isChecked
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isCheckedF,
                                onCheckedChange = { isChecked ->
                                    isCheckedF = isChecked
                                    if (series != null) {
                                        viewModel.setEpisodeChecked(season.season_number,episodeIndex + 1, isChecked,series.id.toString() )
                                    }
                                }
                            )
                            Text(
                                text = "${stringResource(R.string.episode)} ${episodeIndex + 1}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}