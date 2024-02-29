package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens


import android.content.ContentValues.TAG

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cz.mendelu.pef.xchomo.filmbuddy.architecture.BaseViewModel
import cz.mendelu.pef.xchomo.filmbuddy.firebase.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import cz.mendelu.pef.xchomo.filmbuddy.*
import cz.mendelu.pef.xchomo.filmbuddy.MediaItem

import cz.mendelu.pef.xchomo.filmbuddy.api.TmdbApiService.apiService

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AddViewModel(private val auth: AuthRepository) : BaseViewModel() {

    private val _searchResults = MutableStateFlow<List<MediaItem>>(emptyList())
    val searchResults: StateFlow<List<MediaItem>> = _searchResults

    private val _tvSeries = MutableStateFlow<List<MediaItem>>(emptyList())
    val tvSeries: StateFlow<List<MediaItem>> = _tvSeries

    private val _finalResult = MutableStateFlow<List<MediaItem>>(emptyList())
    val finalResult: StateFlow<List<MediaItem>> = _finalResult

    val movieDataStore = MovieDataStore(FilmBuddyApplication.appContext)




    fun saveToDataStore(movie: MediaItem) {
        Log.d("TLAK", movie.toString())
        viewModelScope.launch {
            movieDataStore.saveMedia(movie)
        }
    }

    var apiKey = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3MDU1N2Q3M2ExYjA4MDY4MTVjNzVkZjQxZDJiMTg3NCIsInN1YiI6IjY0NTRmZjVjODdhMjdhMDE3MjNkNTg3NCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.ScGJftf0X06cSuOGKeg-yFRXoucdoLQogF5ev7b6q_o"
    val searchQuery = MutableStateFlow("")

    fun updateSearchQuery(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun searchMedia() {
        viewModelScope.launch(Dispatchers.IO) {
            getTVSeries(searchQuery.value)
            getMovies(searchQuery.value)
            combineResults()
        }
    }

    private fun getMovies(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiService.searchMovies(apiKey, query)
            if (response.isSuccessful) {
                val movieResults = response.body()?.results ?: emptyList()
                val movies = movieResults.map { movie ->
                    MediaItem(movie.id,movie.title,movie.overview,false,movie.poster_path,true,
                        SeasonsList(emptyList())
                    )
                }
                _searchResults.value = movies
            }
        }
    }

    private fun getTVSeries(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.searchTVSeries(apiKey, query)
                if (response.isSuccessful) {
                    val tvSeriesResults = response.body()?.results.orEmpty()
                    val mediaItems = tvSeriesResults.mapNotNull { tvSeries ->
                        val tvSeriesId = tvSeries.id.toString()
                        val tvSeriesResponse = apiService.getTVSeries(tvSeriesId.toInt(), apiKey)
                        if (tvSeriesResponse.isSuccessful) {
                            val seasons = tvSeriesResponse.body()?.seasons?.map {
                                Season(it.season_number, it.episode_count)
                            } ?: emptyList()
                            MediaItem(
                                tvSeries.id,
                                tvSeries.name,
                                tvSeries.overview,
                                false,
                                tvSeries.poster_path,
                                false,
                                SeasonsList(seasons)
                            )
                        } else {
                            null
                        }
                    }
                    withContext(Dispatchers.Main) {
                        _tvSeries.value = mediaItems
                    }
                } else {
                    Log.e("Error", "Failed to retrieve TV series list: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Error", "Exception occurred: $e")
            }
        }
    }



    private fun combineResults() {

        val combinedResults = searchResults.value + tvSeries.value
        _finalResult.value = combinedResults
    }

    fun isInCollection(mediaItem: MediaItem): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val user = auth.getCurrentUser()
        val db = FirebaseFirestore.getInstance()
        val likedFilmsRef = user?.let { it.email?.let { email -> db.collection("account").document(email).collection("LikedFilms") } }
        if (likedFilmsRef != null) {
            likedFilmsRef.whereEqualTo("id", mediaItem.id).get()
                .addOnSuccessListener { documents ->
                    result.value = !documents.isEmpty
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
        return result
    }



    fun addToMyCollection(mediaItem: MediaItem) {
        val user = auth.getCurrentUser()
        if (user != null) {
            val userEmail = user.email ?: run {
                Log.e("Error", "User email is null")
                return
            }

            val db = FirebaseFirestore.getInstance()
            val likedFilmsRef = db.collection("account")
                .document(userEmail)
                .collection("LikedFilms")
                .document(mediaItem.id.toString())

            val seasonData = mediaItem.seasonsList?.seasons?.map { season ->
                mapOf(
                    "season_number" to season.season_number,
                    "episode_count" to season.episode_count
                )
            }

            val filmData = hashMapOf(
                "id" to mediaItem.id,
                "movieLink" to "https://www.themoviedb.org/movie/${mediaItem.id}",
                "overview" to mediaItem.overview,
                "poster_path" to mediaItem.poster_path,
                "title" to mediaItem.title,
                "mediaType" to mediaItem.mediaType,
                "seasons" to seasonData,
                "isChecked" to false
            )

            likedFilmsRef.set(filmData)
                .addOnSuccessListener {
                    Log.d("Firestore", "Document added successfully with ID: ${mediaItem.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error adding document", e)
                }
        } else {
            Log.e("Error", "User is null")
        }
    }




}
