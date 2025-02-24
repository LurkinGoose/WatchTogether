package com.example.watch_together.models

import android.content.Context
import android.content.SharedPreferences
import com.example.watch_together.movieApiService.MovieApiService

class FavoriteMovieRepository(
    private val movieApiService: MovieApiService,
    private val favoriteMovieDao: FavoriteMovieDao,
    context: Context,
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
) {

    fun getHasViewedFavorites(): Boolean {
        return sharedPreferences.getBoolean("hasViewedFavorites", false)
    }
    fun setHasViewedFavorites(value: Boolean) {
        sharedPreferences.edit().putBoolean("hasViewedFavorites", value).apply()
    }
    suspend fun addFavorite(movieId: Int) {
        val addedAt = System.currentTimeMillis()
        favoriteMovieDao.addFavorite(FavoriteMovieEntity(movieId, addedAt))
    }

    suspend fun removeFavorite(movieId: Int) {
        favoriteMovieDao.removeFavorite(FavoriteMovieEntity(movieId))
    }

    suspend fun getFavoriteMovies(): List<Movie> {
        val favoriteIds = favoriteMovieDao.getAllFavorites().map { it.movieId }
        return favoriteIds.map { movieApiService.getMovieById(it) }
    }
}
