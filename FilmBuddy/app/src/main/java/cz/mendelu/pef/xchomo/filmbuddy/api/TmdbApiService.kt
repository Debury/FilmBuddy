package cz.mendelu.pef.xchomo.filmbuddy.api

import cz.mendelu.pef.xchomo.filmbuddy.TmdbApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TmdbApiService {


    private const val BASE_URL = "https://api.themoviedb.org/3/"

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3MDU1N2Q3M2ExYjA4MDY4MTVjNzVkZjQxZDJiMTg3NCIsInN1YiI6IjY0NTRmZjVjODdhMjdhMDE3MjNkNTg3NCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.ScGJftf0X06cSuOGKeg-yFRXoucdoLQogF5ev7b6q_o")
                .build()
            chain.proceed(request)
        }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(TmdbApi::class.java)


}


