package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.elements
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import cz.mendelu.pef.xchomo.filmbuddy.R
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import cz.mendelu.pef.xchomo.filmbuddy.navigation.INavigationRouter
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun BottomNavigationScreen(
    navigation: INavigationRouter,
    currentScreen: String
) {
    val selectedItem = remember { mutableStateOf(getSelectedItemIndex(currentScreen)) }
    val items = listOf("Home", "Films", "Series")
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar {
            items.forEachIndexed { index, item ->
                val icon: Painter = when (index) {
                    0 -> painterResource(id = R.drawable.ic_baseline_home_24)
                    1 -> painterResource(id = R.drawable.ic_baseline_local_movies_24)
                    2 -> painterResource(id = R.drawable.ic_baseline_live_tv_24)
                    else -> painterResource(id = R.drawable.ic_baseline_home_24)
                }

                NavigationBarItem(
                    icon = { Image(painter = icon, contentDescription = "") },
                    label = { Text(item) },
                    selected = selectedItem.value == index,
                    onClick = {
                        selectedItem.value = index
                        when (index) {
                            0 -> navigation.navigateToHomeScreen()
                            1 -> navigation.navigateToFilmScreen()
                            2 -> navigation.navigateToSeriesScreen()
                            else -> navigation.navigateToHomeScreen()
                        }
                    }
                )
            }
        }
    }
}



private fun getSelectedItemIndex(currentScreen: String): Int {
    return when (currentScreen) {
        "Home" -> 0
        "Films" -> 1
        "Series" -> 2
        else -> 0
    }
}
