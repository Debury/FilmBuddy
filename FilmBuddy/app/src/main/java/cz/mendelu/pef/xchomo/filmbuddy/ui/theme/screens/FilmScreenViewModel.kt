package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import cz.mendelu.pef.xchomo.filmbuddy.architecture.BaseViewModel
import cz.mendelu.pef.xchomo.filmbuddy.firebase.AuthRepository
import kotlinx.coroutines.launch
import cz.mendelu.pef.xchomo.filmbuddy.FilmBuddyApplication
import cz.mendelu.pef.xchomo.filmbuddy.MediaItem
import cz.mendelu.pef.xchomo.filmbuddy.SeasonsList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await


class FilmScreenViewModel(private val auth: AuthRepository) : BaseViewModel() {
    val movieDataStore = MovieDataStore(FilmBuddyApplication.appContext)
    var _state = mutableStateOf<FilmScreenUIState>(FilmScreenUIState.Default)

    val movieIdFlow: Flow<Int> = movieDataStore.mediaIdFlow
    val movieTitleFlow: Flow<String> = movieDataStore.mediaTitleFlow
    val movieOverviewFlow: Flow<String> = movieDataStore.mediaOverviewFlow
    val moviePosterPathFlow: Flow<String> = movieDataStore.mediaPosterPathFlow
    val movieTypeFlow: Flow<Boolean> = movieDataStore.mediaTypeFlow
    val movieSeasonsFlow: Flow<SeasonsList> = movieDataStore.seasonsListFlow
    val movieIsCheckedFlow: Flow<Boolean> = movieDataStore.mediaIsCheckedFlow

    val db = FirebaseFirestore.getInstance()



    fun saveToDataStore(movie: MediaItem){

        viewModelScope.launch {
            movieDataStore.saveMedia(movie)
        }
    }

    suspend fun isInCollection(movieId: String): Boolean {
        val user = auth.getCurrentUser()
        val userEmail = user?.email ?: run {
            Log.e("Error", "User email is null")
            return false
        }

        val docRef = db.collection("account")
            .document(userEmail)
            .collection("LikedFilms")
            .document(movieId)

        return try {
            val documentSnapshot = docRef.get().await()
            documentSnapshot.exists()
        } catch (e: Exception) {
            false
        }
    }

    fun fetchFilms(series: Boolean) {
        viewModelScope.launch {
            try {
                val fetchedMediaItems = if (series) {
                    auth.getSeries()
                } else {
                    auth.getFilms()
                }
                Log.d("OK", fetchedMediaItems.toString())
                _state.value = FilmScreenUIState.Success(fetchedMediaItems)
            } catch (e: Exception) {
                // Handle error state if needed
            }
        }
    }
    fun setCheckedInFirestore(filmId: String) {
        val user = auth.getCurrentUser()
        val userEmail = user?.email ?: run {
            Log.e("Error", "User email is null")
            return
        }
        val docRef = db.collection("account")
            .document(userEmail)
            .collection("LikedFilms")
            .document(filmId)

        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val isChecked = documentSnapshot.getBoolean("isChecked") ?: false
                val updateDocument = hashMapOf(
                    "isChecked" to !isChecked // Invert the value of isChecked
                )

                docRef.update(updateDocument as Map<String, Any>)
                    .addOnSuccessListener {
                        // Update successful
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Error", "Error updating document", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("Error", "Error getting document", exception)
            }
    }

    fun setEpisodeChecked(seasonNumber: Int, episodeNumber: Int, isChecked: Boolean, filmId: String) {
        val user = auth.getCurrentUser()
        val userEmail = user?.email ?: run {
            Log.e("Error", "User email is null")
            return
        }
        val docRef = getEpisodeDocumentReference(filmId, episodeNumber.toString(), seasonNumber)

        val episodeData = hashMapOf(
            "isEpisodeChecked" to isChecked
        )

        docRef.set(episodeData as Map<String, Any>)
            .addOnSuccessListener {

            }
            .addOnFailureListener { exception ->
                Log.e("Error", "Error creating or updating document", exception)
            }
    }

    fun deleteFromFirestore(seriesId: String) {
        val user = auth.getCurrentUser()
        val userEmail = user?.email ?: run {
            Log.e("Error", "User email is null")
            return
        }

        val docRef = db.collection("account")
            .document(userEmail)
            .collection("LikedFilms")
            .document(seriesId)

        docRef.delete()
            .addOnSuccessListener {


            }
            .addOnFailureListener { exception ->
                Log.e("Error", "Error deleting document", exception)
            }
    }

    fun getEpisodeDocumentReference(filmId: String, episodeId: String, seasonNumber: Int): DocumentReference {
        val accountId = auth.getCurrentUser()?.email ?: run {
            Log.e("Error", "User email is null")
            return db.collection("account").document().collection("LikedFilms").document()
        }
        val accountRef = db.collection("account").document(accountId)
        val likedFilmsRef = accountRef.collection("LikedFilms").document(filmId)
        val episodesRef = likedFilmsRef.collection(seasonNumber.toString())

        // Create the episodes collection if it doesn't exist


        return episodesRef.document(episodeId)
    }


    suspend fun isEpisodeChecked(seasonId: String, episodeNumber: Int, seasonNumber: Int): Boolean {
        val episodeDocRef = getEpisodeDocumentReference(seasonId, episodeNumber.toString(), seasonNumber)
        return try {
            val documentSnapshot = episodeDocRef.get().await()
            if (documentSnapshot.exists()) {
                val isChecked = documentSnapshot.getBoolean("isEpisodeChecked")
                isChecked ?: false
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun isInCollectionChecked(filmId: String, callback: (Boolean) -> Unit) {
        val userId = auth.getCurrentUser()?.email
        if (userId != null) {
            db.collection("account")
                .document(userId)
                .collection("LikedFilms")
                .document(filmId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val isChecked = documentSnapshot?.getBoolean("isChecked") ?: false
                    callback(isChecked)
                }
                .addOnFailureListener { exception ->
                    // Handle error state if needed
                    callback(false)
                }
        } else {
            callback(false)
        }
    }
}
