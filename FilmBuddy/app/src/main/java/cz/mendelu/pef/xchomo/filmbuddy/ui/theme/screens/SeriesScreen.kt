package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens
import android.util.Log
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
import cz.mendelu.pef.xchomo.filmbuddy.MediaItem
import cz.mendelu.pef.xchomo.filmbuddy.R
import cz.mendelu.pef.xchomo.filmbuddy.navigation.INavigationRouter
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.elements.BottomNavigationScreen
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens.FilmScreenUIState
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens.FilmScreenViewModel

import org.koin.androidx.compose.getViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesScreen(
    navigation: INavigationRouter,
    viewModel: FilmScreenViewModel = getViewModel(),
) {
    val series = remember { mutableStateListOf<MediaItem>() }

    viewModel._state.value.let { state ->
        when (state) {
            FilmScreenUIState.Default -> {
                viewModel.fetchFilms(series = true)
            }
            is FilmScreenUIState.Success -> {
                series.clear()
                series.addAll(state.movies)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(stringResource(R.string.app_name))
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
            SeriesScreenContent(navigation, viewModel, series, paddingValues = it)

            BottomNavigationScreen(navigation = navigation, currentScreen = "Series")
        }
    }
}

@Composable
fun SeriesScreenContent(
    navigation: INavigationRouter,
    viewModel: FilmScreenViewModel,
    series: List<MediaItem>,
    paddingValues: PaddingValues
) {
    if (series.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.empty_series),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(R.string.tap_s),
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
            items(series) { seriesItem ->
                SeriesCard(navigation = navigation, seriesItem = seriesItem, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SeriesCard(
    navigation: INavigationRouter,
    seriesItem: MediaItem,
    viewModel: FilmScreenViewModel
) {
    var isChecked by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = seriesItem.id) {
        viewModel.isInCollectionChecked(seriesItem.id.toString()) { isInCollection ->
            isChecked = isInCollection
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                viewModel.saveToDataStore(seriesItem); navigation.navigateToDetailSeriesScreen()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(100.dp)
                    .clip(shape = RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop,
                painter = rememberImagePainter(
                    data = "https://image.tmdb.org/t/p/w500${seriesItem.poster_path}",
                    builder = {
                        size(100, 100)
                    }
                ),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = seriesItem.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Checkbox(
                checked = isChecked,
                onCheckedChange = { checked ->
                    isChecked = checked
                    if (checked) {
                        viewModel.setCheckedInFirestore(seriesItem.id.toString())
                    } else {
                        viewModel.setCheckedInFirestore(seriesItem.id.toString())
                    }
                }
            )
        }
    }
}
