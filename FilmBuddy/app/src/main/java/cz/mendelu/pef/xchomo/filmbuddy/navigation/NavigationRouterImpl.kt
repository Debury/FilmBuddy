package cz.mendelu.pef.xchomo.filmbuddy.navigation

import androidx.navigation.NavController

class NavigationRouterImpl(
    private val navController: NavController) : INavigationRouter {

    override fun returnBack() {
        navController.popBackStack()
    }

    override fun getNavController(): NavController {
        return navController
    }

    override fun navigateToRegisterScreen() {
        navController.navigate( Destination.RegisterScreen.route)
    }

    override fun navigateToHomeScreen() {
        navController.navigate( Destination.HomeScreen.route)
    }

    override fun navigateToLoginScreen() {
        navController.navigate( Destination.LoginScreen.route)
    }

    override fun navigateToAddFilmScreen() {
        navController.navigate( Destination.AddFilmScreen.route)
    }

    override fun navigateToFilmScreen() {
        navController.navigate( Destination.FilmScreen.route)
    }

    override fun navigateToDetailFilmScreen() {
        navController.navigate(Destination.DetailFilmScreen.route)
    }

    override fun navigateToSeriesScreen() {
        navController.navigate(Destination.SeriesScreen.route)
    }

    override fun navigateToDetailSeriesScreen() {
        navController.navigate(Destination.DetailSeriesScreen.route)
    }


}
