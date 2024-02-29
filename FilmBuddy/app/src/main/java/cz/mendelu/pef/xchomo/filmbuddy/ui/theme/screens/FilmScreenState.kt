package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens

import cz.mendelu.pef.xchomo.filmbuddy.MediaItem


sealed class FilmScreenUIState {
    object Default : FilmScreenUIState()
    class Success(val movies: List<MediaItem>) : FilmScreenUIState()
}