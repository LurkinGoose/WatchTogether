package com.example.watch_together

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val BASE_URL = "https://api.themoviedb.org/3/"
private const val API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJjYTEwNzNhZGQ2YzQ3ZWFmZDE1MWFiODZjMjFjYTRiYiIsIm5iZiI6MTczOTM2MzIzMi4yNDQsInN1YiI6IjY3YWM5M2EwZGQ5YjdkMmVhYWIwY2Q0MyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.OaP36gO8NUw8__ZWzOpjqjifHlmiSN718JJjjIrl1Wo"

interface MovieApiService {

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("language") language: String = "ru-RU"
        ): MovieResponse

//    @GET("configuration")
//    suspend fun getConfiguration(
//        @Query("api_key") apiKey: String = API_KEY
//    ): ConfigurationResponse

}

object RetrofitInstance {

    private val client = okhttp3.OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Authorization", "Bearer $API_KEY")
                .build()
            chain.proceed(request)
        }
        .build()

    val api: MovieApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(MovieApiService::class.java)
    }
}

