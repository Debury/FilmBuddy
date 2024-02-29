package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.elements


import androidx.compose.foundation.layout.Row

import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens.AddViewModel
import org.koin.androidx.compose.getViewModel


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.livedata.observeAsState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import coil.compose.rememberImagePainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import cz.mendelu.pef.xchomo.filmbuddy.MediaItem
import cz.mendelu.pef.xchomo.filmbuddy.R

@Composable
fun MovieItem(movie: MediaItem, viewModel: AddViewModel = getViewModel()) {
    var addToCollection by remember { mutableStateOf(false) }
    var visibility by remember { mutableStateOf(true) }

    val inCollection = viewModel.isInCollection(movie).observeAsState(initial = false).value
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (movie.poster_path != null) {
            Image(
                painter = rememberImagePainter(
                    data = "https://image.tmdb.org/t/p/w200${movie.poster_path}",
                    builder = {

                    }
                ),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(140.dp, 140.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Image(
                painter = rememberImagePainter(
                    data = R.drawable._758832,
                    builder = {

                    }
                ),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(140.dp, 140.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (movie.title.length > 20) "${movie.title.take(20)}..." else movie.title,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp) // Add padding to the left side of the title
            )
        }
        Spacer(modifier = Modifier.weight(1f)) // Add spacer to push the plus icon to the right
        IconButton(
            onClick = {
                addToCollection = true
                if (inCollection) {
                    visibility = false
                }
            },
            enabled = !inCollection && !addToCollection
        ) {
            Icon(
                imageVector = if (inCollection) Icons.Default.Done else Icons.Default.Add,
                contentDescription = if (inCollection) "Added to collection" else "Add to collection",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    LaunchedEffect(addToCollection) {
        if (addToCollection) {
            viewModel.addToMyCollection(movie)
            addToCollection = false
        }
    }
}
