package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import cz.mendelu.pef.xchomo.filmbuddy.MediaItem
import cz.mendelu.pef.xchomo.filmbuddy.SeasonsList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "movie_preferences")

class MovieDataStore(context: Context) {

    private val dataStore: DataStore<Preferences> = context.dataStore

    object Keys {
        val MEDIA_ID = intPreferencesKey("media_id")
        val MEDIA_TITLE = stringPreferencesKey("media_title")
        val MEDIA_OVERVIEW = stringPreferencesKey("media_overview")
        val MEDIA_POSTER_PATH = stringPreferencesKey("media_poster_path")
        val MEDIA_TYPE = booleanPreferencesKey("media_type")
        val SEASONS_LIST = stringPreferencesKey("seasons_list")
        val IS_CHECKED = booleanPreferencesKey("is_checked")
    }

    val mediaIdFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[Keys.MEDIA_ID] ?: 0
    }

    val mediaTitleFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[Keys.MEDIA_TITLE] ?: ""
    }

    val mediaOverviewFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[Keys.MEDIA_OVERVIEW] ?: ""
    }

    val mediaPosterPathFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[Keys.MEDIA_POSTER_PATH] ?: ""
    }

    val mediaTypeFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.MEDIA_TYPE] ?: false
    }

    val seasonsListFlow: Flow<SeasonsList> = dataStore.data.map { preferences ->
        val seasonsListString = preferences[Keys.SEASONS_LIST] ?: ""
        SeasonsList.deserialize(seasonsListString) ?: SeasonsList(emptyList())
    }

    val mediaIsCheckedFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.IS_CHECKED] ?: false
    }

    suspend fun saveMedia(mediaItem: MediaItem) {
        dataStore.edit { preferences ->
            preferences[Keys.MEDIA_ID] = mediaItem.id
            preferences[Keys.MEDIA_TITLE] = mediaItem.title
            preferences[Keys.MEDIA_OVERVIEW] = mediaItem.overview
            preferences[Keys.MEDIA_POSTER_PATH] = mediaItem.poster_path ?: ""
            preferences[Keys.MEDIA_TYPE] = mediaItem.mediaType
            preferences[Keys.SEASONS_LIST] = mediaItem.seasonsList?.serialize() ?: ""
            preferences[Keys.IS_CHECKED] = mediaItem.isChecked
        }
    }
}
