package cz.mendelu.pef.xchomo.filmbuddy



import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import cz.mendelu.pef.xchomo.filmbuddy.navigation.INavigationRouter
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.elements.BottomNavigationScreen
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens.FilmScreenUIState
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens.FilmScreenViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmScreen(
    navigation: INavigationRouter,
    viewModel: FilmScreenViewModel = getViewModel(),
) {
    val movies = remember { mutableStateListOf<MediaItem>() }



    viewModel._state.value.let { state ->
        when (state) {
            FilmScreenUIState.Default -> {
                viewModel.fetchFilms(series = false)
            }
            is FilmScreenUIState.Success -> {
                movies.clear()
                movies.addAll(state.movies)


            }
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("FilmBuddy")
            })
        },
        floatingActionButton = {
            SmallFloatingActionButton(
                onClick = { navigation.navigateToAddFilmScreen() },
                modifier = Modifier
                    .padding(bottom = 76.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        },





    ) {
        Box {

                FilmScreenContent(navigation, viewModel, movies, paddingValues = it)



            }
        BottomNavigationScreen(navigation = navigation, currentScreen = "Films")
        }



    }






@Composable
fun FilmScreenContent(
    navigation: INavigationRouter,
    viewModel: FilmScreenViewModel,
    movies: List<MediaItem>,
    paddingValues: PaddingValues
) {
    if (movies.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.empty_film),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(R.string.tap),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // You can add an image or any other creative element here
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp),
            contentPadding = paddingValues
        ) {
            items(movies) { movie ->
                FilmCard(navigation = navigation, movie = movie, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FilmCard(
    navigation: INavigationRouter,
    movie: MediaItem,
    viewModel: FilmScreenViewModel
) {
    var isChecked by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = movie.id) {
        viewModel.isInCollectionChecked(movie.id.toString()) { isInCollection ->
            isChecked = isInCollection
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                viewModel.saveToDataStore(movie)
                navigation.navigateToDetailFilmScreen()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier
                    .size(100.dp)
                    .clip(shape = RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop,
                painter = rememberImagePainter(
                    data = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
                    builder = {
                        size(100, 100)
                    }
                ),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Checkbox(
                checked = isChecked,
                onCheckedChange = { checked ->
                    isChecked = checked
                    if (checked) {
                        viewModel.setCheckedInFirestore(movie.id.toString())
                    } else {
                        viewModel.setCheckedInFirestore(movie.id.toString())
                    }
                }
            )
        }
    }
}

