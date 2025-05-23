package com.example.watch_together.movieApiService

import com.example.watch_together.BuildConfig
import com.example.watch_together.models.CombinedCreditsResponse
import com.example.watch_together.models.ContentRatingsResponse
import com.example.watch_together.models.CreditsResponse
import com.example.watch_together.models.ImagesResponse
import com.example.watch_together.models.Movie
import com.example.watch_together.models.MovieList
import com.example.watch_together.models.PersonDetailsResponse
import com.example.watch_together.models.RecommendationResponse
import com.example.watch_together.models.ReleaseDatesResponse
import com.example.watch_together.models.Series
import com.example.watch_together.models.SeriesList
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://api.themoviedb.org/3/"
private const val API_KEY = BuildConfig.TMDB_API_KEY

interface MovieApiService {

    // Персоны

    @GET("person/{person_id}")
    suspend fun getPersonById(
        @Path("person_id") personId: Int,
        @Query("language") language: String = "ru-RU"
    ): PersonDetailsResponse

    @GET("person/{person_id}/combined_credits")
    suspend fun getPersonCombinedCredits(
        @Path("person_id") personId: Int,
        @Query("language") language: String = "ru"
    ): CombinedCreditsResponse


    // Фильмы

    // Получения списка фильмов по запросу
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("language") language: String = "ru-RU"
        ): MovieList

    // Запрос для получения id фильма
    @GET("movie/{movie_id}")
    suspend fun getMovieById(
        @retrofit2.http.Path("movie_id") movieId: Int,
        @Query("language") language: String = "ru-RU"
    ): Movie

    // Запрос для получения топ рейтинга фильмов
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "ru-RU"
    ): MovieList

    // Популярные фильмы
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "ru-RU"
    ): MovieList

    // Сейчас в кинотеатрах
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "ru-RU"
    ): MovieList

    // Скоро в прокате
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "ru-RU"
    ): MovieList

    // Трендовые фильмы (за неделю)
    @GET("trending/movie/{time_window}")
    suspend fun getTrendingMovies(
        @retrofit2.http.Path("time_window") timeWindow: String = "week",
        @Query("page") page: Int = 1,
        @Query("language") language: String = "ru-RU"
    ): MovieList

    // Рейтинг фильмов
    @GET("movie/{movie_id}/release_dates")
    suspend fun getMovieReleaseDates(
        @Path("movie_id") movieId: Int
    ): ReleaseDatesResponse

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "ru"
    ): CreditsResponse

    @GET("movie/{movie_id}/images")
    suspend fun getMovieImages(
        @Path("movie_id") movieId: Int,
        @Query("include_image_language") includeLang: String = "ru,null",
        @Query("language") language: String = "ru"
    ): ImagesResponse

//    @GET("movie/{movie_id}/similar")
//    suspend fun getSimilarMovies(
//        @Path("movie_id") movieId: Int,
//        @Query("language") language: String = "ru-RU",
//        @Query("page") page: Int = 1
//    ): SimilarResponse

    @GET("movie/{movie_id}/recommendations")
    suspend fun getMovieRecommendations(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "ru-RU",
        @Query("page") page: Int = 1
    ): RecommendationResponse



    // Сериалы

    // Получения списка сериалов по запросу
    @GET("search/tv")
    suspend fun searchTvShows(
        @Query("query") query: String,
        @Query("language") language: String = "ru-RU"
    ): SeriesList

    // Запрос для получения id сериала
    @GET("tv/{tv_id}")
    suspend fun getTvShowsById(
        @retrofit2.http.Path("tv_id") movieId: Int,
        @Query("language") language: String = "ru-RU"
    ): Series

    // Популярные сериалы
    @GET("tv/popular")
    suspend fun getPopularTvShows(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "ru-RU"
    ): SeriesList

    // Топ рейтинга сериалов
    @GET("tv/top_rated")
    suspend fun getTopRatedTvShows(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "ru-RU"
    ): SeriesList

    // Сейчас в эфире (On the air)
    @GET("tv/on_the_air")
    suspend fun getOnTheAirTvShows(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "ru-RU"
    ): SeriesList

    // Трендовые сериалы (за неделю)
    @GET("trending/tv/{time_window}")
    suspend fun getTrendingTvShows(
        @retrofit2.http.Path("time_window") timeWindow: String = "week",
        @Query("page") page: Int = 1,
        @Query("language") language: String = "ru-RU"
    ): SeriesList

    // Рейтинг сериалов
    @GET("tv/{tv_id}/content_ratings")
    suspend fun getTvContentRatings(
        @Path("tv_id") tvId: Int
    ): ContentRatingsResponse

    @GET("tv/{tv_id}/credits")
    suspend fun getTvCredits(
        @Path("tv_id") seriesId: Int,
        @Query("language") language: String = "ru"
    ): CreditsResponse

    @GET("tv/{tv_id}/images")
    suspend fun getTvImages(
        @Path("tv_id") tvId: Int,
        @Query("include_image_language") includeLang: String = "ru,null",
        @Query("language") language: String = "ru"
    ): ImagesResponse

//    @GET("tv/{tv_id}/similar")
//    suspend fun getSimilarTvShows(
//        @Path("tv_id") tvId: Int,
//        @Query("language") language: String = "ru-RU",
//        @Query("page") page: Int = 1
//    ): SimilarResponse

    @GET("tv/{tv_id}/recommendations")
    suspend fun getTvRecommendations(
        @Path("tv_id") tvId: Int,
        @Query("language") language: String = "ru-RU",
        @Query("page") page: Int = 1
    ): RecommendationResponse



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

