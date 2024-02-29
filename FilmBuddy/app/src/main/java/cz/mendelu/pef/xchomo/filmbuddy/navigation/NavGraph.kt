package cz.mendelu.pef.xchomo.filmbuddy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.mendelu.pef.xchomo.filmbuddy.FilmScreen
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens.DetailedFilmScreen
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens.*

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    navigation: INavigationRouter = remember {
        NavigationRouterImpl(navController)
    },
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Destination.RegisterScreen.route) {
            RegisterScreen(navigation)
        }
        composable(Destination.AddFilmScreen.route) {
            AddFilmScreen(navigation)
        }
        composable(Destination.LoginScreen.route) {
            LoginScreen(navigation)
        }
        composable(Destination.HomeScreen.route) {
            HomeScreen(navigation)
        }
        composable(Destination.FilmScreen.route) {
            FilmScreen(navigation)
        }
        composable(Destination.DetailFilmScreen.route) {
            DetailedFilmScreen(navigation)
        }
        composable(Destination.SeriesScreen.route) {
            SeriesScreen(navigation)
        }
        composable(Destination.DetailSeriesScreen.route) {
            DetailedSavedSeriesScreen(navigation)
        }
    }
}
