package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import cz.mendelu.pef.xchomo.filmbuddy.navigation.INavigationRouter
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.elements.BackArrowScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import cz.mendelu.pef.xchomo.filmbuddy.MediaItem
import cz.mendelu.pef.xchomo.filmbuddy.R
import cz.mendelu.pef.xchomo.filmbuddy.SeasonsList
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.elements.BottomNavigationScreen
import kotlinx.coroutines.flow.combine
import org.koin.androidx.compose.getViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedFilmScreen(
    navigation: INavigationRouter,
    viewModel: FilmScreenViewModel = getViewModel(),
) {
    val movie = combine(
        viewModel.movieIdFlow,
        viewModel.movieTitleFlow,
        viewModel.movieOverviewFlow,
        viewModel.movieIsCheckedFlow,
        viewModel.moviePosterPathFlow,
        viewModel.movieTypeFlow,
        viewModel.movieSeasonsFlow
    ) { values ->
        MediaItem(
            values[0] as Int,
            values[1] as String,
            values[2] as String,
            values[3] as Boolean,
            values[4] as String,
            values[5] as Boolean,
            values[6] as SeasonsList
        )
    }.collectAsState(initial = MediaItem(0, "", "", false,"", true, SeasonsList(emptyList())))
    var isCheckedF by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = movie.value.id) {
        viewModel.isInCollectionChecked(movie.value.id.toString()) { isInCollection ->
            isCheckedF = isInCollection
        }
    }
    var isInCollectionF by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = movie.value.id) {
        val isInCollection = viewModel.isInCollection(movie.value.id.toString())
        isInCollectionF = isInCollection

    }
    Log.d("DetailedFilmScreen", "isInCollectionF: $isInCollectionF")
    BackArrowScreen(
        topBarTitle = stringResource(R.string.app_name),
        onBackClick = { navigation.returnBack() },
        drawFullScreenContent = true,
        showCheckbox = (isInCollectionF && movie.value.mediaType),
        isChecked = isCheckedF,
        onDeleteClick = {
            viewModel.deleteFromFirestore(movie.value.id.toString())
            navigation.navigateToFilmScreen()
        },
        onCheckedChange = { isChecked ->
            viewModel.setCheckedInFirestore(movie.value.id.toString())
            isCheckedF = isChecked
        }

    ) {
        DetailedFilmScreenContent(movie = movie.value, paddingValues = it)
    }

    BottomNavigationScreen(navigation = navigation, currentScreen = "Film")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedFilmScreenContent(
    movie: MediaItem,
    paddingValues: PaddingValues,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(paddingValues)
    ) {
        BoxWithConstraints {
            val screenHeight = constraints.maxHeight.toFloat()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight.dp - 56.dp) // Subtract the height of the bottom navigation
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
                                painter = rememberImagePainter(data = "https://image.tmdb.org/t/p/w1280${movie.poster_path}"),
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
                            text = movie.title,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = movie.overview,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        if (!movie.mediaType && movie.seasonsList != null) {
                            Text(
                                text = stringResource(R.string.seasons),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
                            )

                            Column {
                                movie.seasonsList!!.seasons?.forEach { season ->
                                    Card(
                                        shape = MaterialTheme.shapes.medium,
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
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
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
