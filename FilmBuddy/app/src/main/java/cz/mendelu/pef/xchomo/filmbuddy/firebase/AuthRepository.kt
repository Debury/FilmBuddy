package cz.mendelu.pef.xchomo.filmbuddy.firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import cz.mendelu.pef.xchomo.filmbuddy.MediaItem
import cz.mendelu.pef.xchomo.filmbuddy.Season
import cz.mendelu.pef.xchomo.filmbuddy.SeasonsList
import kotlinx.coroutines.tasks.await


object AuthRepository {
    private val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()
    fun register(email: String, password: String, username: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                val userDocument = db.collection("account").document(email)
                val data = hashMapOf(
                    "email" to email,
                    "username" to username
                )
                userDocument.set(data)
            }
    }

    fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    fun logout() {
        auth.signOut()
    }
    fun convertToSeasonsList(data: Any?): SeasonsList? {
        return if (data is ArrayList<*>) {
            val seasonsList = mutableListOf<Season>()
            data.forEach { item ->
                if (item is HashMap<*, *>) {
                    val seasonNumber = item["season_number"] as? Int
                    val episodeCount = item["episode_count"] as? Int
                    if (seasonNumber != null && episodeCount != null) {
                        seasonsList.add(Season(seasonNumber, episodeCount))
                    }
                }
            }
            SeasonsList(seasonsList)
        } else {
            null
        }
    }

    suspend fun getFilms(): List<MediaItem> {
        try {
            val currentUserEmail = auth.currentUser?.email ?: ""
            val userQuerySnapshot = db.collection("account").whereEqualTo("email", currentUserEmail).get().await()

            if (!userQuerySnapshot.isEmpty) {
                val userDocRef = userQuerySnapshot.documents[0].reference
                val moviesCollectionRef = userDocRef.collection("LikedFilms")

                val moviesQuerySnapshot = moviesCollectionRef.get().await()

                if (!moviesQuerySnapshot.isEmpty) {
                    return moviesQuerySnapshot.documents.mapNotNull { documentSnapshot ->
                        // Create our MediaItem manually, not through deserialization
                        val mediaType = documentSnapshot.getBoolean("mediaType") ?: false
                        val isChecked = documentSnapshot.getBoolean("isChecked") ?: false
                        if (mediaType) {
                            val id = documentSnapshot.getLong("id")?.toInt() ?: 0
                            val title = documentSnapshot.getString("title") ?: ""
                            val overview = documentSnapshot.getString("overview") ?: ""
                            val poster_path = documentSnapshot.getString("poster_path") ?: ""
                            val seasonsListData = documentSnapshot.get("seasons") as? List<Map<String, Any>>
                            val seasonsList = seasonsListData?.map {
                                Season((it["season_number"] as? Long)?.toInt() ?: 0, (it["episode_count"] as? Long)?.toInt() ?: 0)
                            } ?: listOf()

                            MediaItem(
                                id = id,
                                title = title,
                                overview = overview,
                                isChecked = isChecked,
                                poster_path = poster_path,
                                mediaType = mediaType,
                                seasonsList = SeasonsList(seasonsList)
                            )
                        } else null
                    }
                } else {
                    println("No movies found in the collection.")
                    return emptyList()
                }
            } else {
                println("User not found.")
                return emptyList()
            }
        } catch (error: Exception) {
            println("Error retrieving movies from collection: $error")
            return emptyList()
        }
    }

    suspend fun getSeries(): List<MediaItem> {
        try {
            val currentUserEmail = auth.currentUser?.email ?: ""
            val userQuerySnapshot = db.collection("account").whereEqualTo("email", currentUserEmail).get().await()

            if (!userQuerySnapshot.isEmpty) {
                val userDocRef = userQuerySnapshot.documents[0].reference
                val seriesCollectionRef = userDocRef.collection("LikedFilms")

                val seriesQuerySnapshot = seriesCollectionRef.get().await()

                if (!seriesQuerySnapshot.isEmpty) {
                    return seriesQuerySnapshot.documents.mapNotNull { documentSnapshot ->
                        // Create our MediaItem manually, not through deserialization
                        val mediaType = documentSnapshot.getBoolean("mediaType") ?: false
                        val isChecked = documentSnapshot.getBoolean("isChecked") ?: false
                        if (mediaType == false) {
                            val id = documentSnapshot.getLong("id")?.toInt() ?: 0
                            val title = documentSnapshot.getString("title") ?: ""
                            val overview = documentSnapshot.getString("overview") ?: ""
                            val poster_path = documentSnapshot.getString("poster_path") ?: ""
                            val seasonsListData = documentSnapshot.get("seasons") as? List<Map<String, Any>>
                            val seasonsList = seasonsListData?.map {
                                Season((it["season_number"] as? Long)?.toInt() ?: 0, (it["episode_count"] as? Long)?.toInt() ?: 0)
                            } ?: listOf()

                            MediaItem(
                                id = id,
                                title = title,
                                overview = overview,
                                isChecked = isChecked,
                                poster_path = poster_path,
                                mediaType = mediaType,
                                seasonsList = SeasonsList(seasonsList)
                            )
                        } else null
                    }
                } else {
                    println("No series found in the collection.")
                    return emptyList()
                }
            } else {
                println("User not found.")
                return emptyList()
            }
        } catch (error: Exception) {
            println("Error retrieving series from collection: $error")
            return emptyList()
        }
    }






    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}

