package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.mendelu.pef.xchomo.filmbuddy.R
import cz.mendelu.pef.xchomo.filmbuddy.navigation.INavigationRouter
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.elements.BackArrowScreen
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.elements.BottomNavigationScreen
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.elements.MovieItem
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFilmScreen(
    navigation: INavigationRouter,
    viewModel: AddViewModel = getViewModel()
) {
    BackArrowScreen(topBarTitle = "FilmBuddy", onBackClick = { navigation.navigateToFilmScreen() }, drawFullScreenContent = true) {
        AddFilmScreenContent(navigation = navigation,viewModel = viewModel, paddingValues = it)
    }

    BottomNavigationScreen(navigation = navigation, currentScreen = "Film")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFilmScreenContent(
    navigation: INavigationRouter,
    viewModel: AddViewModel,
    paddingValues: PaddingValues
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.finalResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .padding(bottom = 76.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newValue ->
                viewModel.updateSearchQuery(newValue)
                viewModel.searchMedia()
            },
            label = { Text(stringResource(R.string.search_hint), color = MaterialTheme.colorScheme.onSurface) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_hint),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                cursorColor = MaterialTheme.colorScheme.primary,
                textColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(searchResults) { movie ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .clickable { viewModel.saveToDataStore(movie = movie); navigation.navigateToDetailFilmScreen(); Log.d("PRINT", movie.toString()) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        MovieItem(movie = movie)
                    }
                }
            }
        }
    }
}









