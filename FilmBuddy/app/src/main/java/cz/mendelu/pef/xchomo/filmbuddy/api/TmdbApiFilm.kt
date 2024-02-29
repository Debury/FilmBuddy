package cz.mendelu.pef.xchomo.filmbuddy

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.http.Path

interface TmdbApi {
    @GET("search/tv")
    suspend fun searchTVSeries(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): Response<TVSeriesSearchResult>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): Response<MovieSearchResult>

    @GET("movie/{movie_id}")
    suspend fun getMovie(
        @Query("api_key") apiKey: String,
        @Query("movie_id") movieId: String
    ): Response<MediaItem>

    @GET("tv/{tv_id}")
    suspend fun getTVSeries(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String
    ): Response<TVSeries>
}

data class MediaSearchResult(
    val results: List<MediaItem>
)

data class TVSeriesSearchResult(
    val results: List<TVSeries>
)

data class MovieSearchResult(
    val results: List<Movie>
)

data class MediaItem(
    val id: Int,
    val title: String,
    val overview: String,
    var isChecked: Boolean,
    @SerializedName("poster_path")
    val poster_path: String?,
    @SerializedName("media_type")
    val mediaType: Boolean,
    @SerializedName("seasons")
    @get:PropertyName("seasons")
    @set:PropertyName("seasons")
    var seasonsList: SeasonsList?,
    var episodesChecked: Map<Int, Boolean> = emptyMap() // New property for episode checked state
) {
    constructor() : this(0, "", "", false, null, true, SeasonsList(emptyList()))

    companion object {
        private const val COLLECTION_NAME = "mediaItems"
    }
}

data class SeasonsList(
    val seasons: List<Season>?
){
    fun serialize(): String {
        val jsonArray = JSONArray()
        seasons?.forEach { season ->
            val seasonJson = JSONObject()
            seasonJson.put("season_number", season.season_number)
            seasonJson.put("episode_count", season.episode_count) // store as an integer
            jsonArray.put(seasonJson)
        }
        return jsonArray.toString()
    }
    fun countTotalEpisodes(): Int {
        return seasons?.sumOf { it.episode_count } ?: 0
    }

    companion object {
        fun deserialize(serialized: String): SeasonsList? {
            if (serialized.isBlank()) {
                return null
            }
            return try {
                val jsonArray = JSONArray(serialized)
                val seasonList = mutableListOf<Season>()
                for (i in 0 until jsonArray.length()) {
                    val seasonJson = jsonArray.getJSONObject(i)
                    val seasonNumber = seasonJson.getInt("season_number")
                    val episodeCount = seasonJson.getInt("episode_count") // get as an integer
                    val season = Season(season_number = seasonNumber, episode_count = episodeCount)
                    seasonList.add(season)
                }
                SeasonsList(seasonList)
            } catch (e: JSONException) {
                // Log the exception, return null, or throw a custom exception, depending on your use case
                null
            }
        }
    }
}

data class Season(
    @SerializedName("season_number")
    val season_number: Int,
    @SerializedName("episode_count")
    val episode_count: Int
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "season_number" to season_number,
            "episode_count" to episode_count
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): Season {
            val seasonNumber = map["season_number"] as? Int ?: 0
            val episodeCount = map["episode_count"] as? Int ?: 0
            return Season(seasonNumber, episodeCount)
        }
    }
}

data class TVSeries(
    val id: Int,
    val name: String,
    val overview: String,
    @SerializedName("poster_path")
    val poster_path: String,
    @SerializedName("seasons")
    val seasons: List<Season>
)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String,
    val mediaType: Boolean,
    val seasons: List<Season>
) {
    constructor() : this(0, "", "", "", true, emptyList())
}


