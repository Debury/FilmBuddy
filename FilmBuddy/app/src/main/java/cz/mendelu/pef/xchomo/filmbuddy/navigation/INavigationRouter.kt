package cz.mendelu.pef.xchomo.filmbuddy.navigation

import androidx.navigation.NavController


interface INavigationRouter {
    fun returnBack()
    fun getNavController(): NavController
    fun navigateToRegisterScreen()
    fun navigateToHomeScreen()
    fun navigateToLoginScreen()
    fun navigateToAddFilmScreen()
    fun navigateToFilmScreen()
    fun navigateToDetailFilmScreen()
    fun navigateToSeriesScreen()
    fun navigateToDetailSeriesScreen()

}
