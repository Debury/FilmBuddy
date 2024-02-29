package cz.mendelu.pef.xchomo.filmbuddy.navigation


sealed class Destination(val route: String) {
    object MainScreen : Destination(route = "main_screen")
    object RegisterScreen : Destination(route = "register_screen")
    object HomeScreen: Destination(route = "home_screen")
    object LoginScreen: Destination(route = "login_screen")
    object AddFilmScreen: Destination(route = "add_film_screen")
    object FilmScreen: Destination(route = "film_screen")
    object DetailFilmScreen: Destination(route = "detail_film_screen")
    object SeriesScreen: Destination(route = "series_screen")
    object DetailSeriesScreen: Destination(route = "detail_series_screen")

}

