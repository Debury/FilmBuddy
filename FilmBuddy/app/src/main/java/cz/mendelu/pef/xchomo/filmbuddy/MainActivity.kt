package cz.mendelu.pef.xchomo.filmbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import cz.mendelu.pef.xchomo.filmbuddy.navigation.Destination
import cz.mendelu.pef.xchomo.filmbuddy.navigation.NavGraph
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.FilmBuddyTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FilmBuddyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(startDestination = Destination.LoginScreen.route)
                }
            }
        }
    }
}

