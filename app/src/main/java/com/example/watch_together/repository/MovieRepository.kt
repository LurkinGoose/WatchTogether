package com.example.watch_together.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.watch_together.models.FavoriteMovieDao
import com.example.watch_together.models.FavoriteMovieEntity
import com.example.watch_together.models.Movie
import com.example.watch_together.movieApiService.MovieApiService

class MovieRepository(
    private val apiService: MovieApiService,
    private val favoriteMovieDao: FavoriteMovieDao,
    context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getHasViewedFavorites(): Boolean {
        return sharedPreferences.getBoolean("hasViewedFavorites", false)
    }

    fun setHasViewedFavorites(value: Boolean) {
        sharedPreferences.edit().putBoolean("hasViewedFavorites", value).apply()
    }

    // Добавление фильма в избранное
    suspend fun addFavorite(movieId: Int) {
        favoriteMovieDao.addFavorite(FavoriteMovieEntity(movieId, System.currentTimeMillis()))
    }

    // Удаление фильма из избранного
    suspend fun removeFavorite(movieId: Int) {
        favoriteMovieDao.removeFavorite(FavoriteMovieEntity(movieId))
    }

    // Получение списка избранных фильмов
    suspend fun getFavoriteMovies(): List<Movie> {
        val favoriteIds = favoriteMovieDao.getAllFavorites().map { it.movieId }
        return favoriteIds.map { apiService.getMovieById(it) }
    }

    // Получение деталей фильма
    suspend fun getMovieDetails(movieId: Int): Movie {
        return apiService.getMovieById(movieId)
    }

    // Поиск фильмов по названию
    suspend fun searchMovies(query: String): List<Movie> {
        return apiService.searchMovies(query).results
    }
}
