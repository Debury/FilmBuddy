package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.mendelu.pef.xchomo.filmbuddy.navigation.INavigationRouter
import cz.mendelu.pef.xchomo.filmbuddy.R
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.elements.BottomNavigationScreen

@Composable
fun HomeScreen(
    navigation: INavigationRouter
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { navigation.navigateToFilmScreen() },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.film))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { navigation.navigateToSeriesScreen() },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.series))
            }
        }
    }

    BottomNavigationScreen(navigation = navigation, currentScreen = "Home")
}
