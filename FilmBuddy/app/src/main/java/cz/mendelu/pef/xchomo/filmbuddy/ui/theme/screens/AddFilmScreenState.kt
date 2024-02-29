package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens

import cz.mendelu.pef.xchomo.filmbuddy.MediaItem


sealed class AddFilmScreenState {
    object Loading : AddFilmScreenState()
    data class Success(val searchResults: List<MediaItem>) : AddFilmScreenState()
    data class Error(val message: String) : AddFilmScreenState()
}