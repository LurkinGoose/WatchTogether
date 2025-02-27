package com.example.watch_together.repository

import com.example.watch_together.models.FavoriteMovieDao
import com.example.watch_together.models.FavoriteMovieEntity
import com.example.watch_together.models.Movie
import com.example.watch_together.movieApiService.MovieApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val apiService: MovieApiService,
    private val favoriteMovieDao: FavoriteMovieDao
) {
    suspend fun searchMovies(query: String): List<Movie> {
        return apiService.searchMovies(query).results
    }

    suspend fun getMovieDetails(movieId: Int): Movie {
        return apiService.getMovieById(movieId)
    }

    suspend fun addToFavorites(movieId: Int) {
        favoriteMovieDao.addFavorite(FavoriteMovieEntity(movieId))
    }

    suspend fun removeFromFavorites(movieId: Int) {
        favoriteMovieDao.removeFavorite(FavoriteMovieEntity(movieId))
    }

    fun getAllFavorites(): Flow<List<Movie>> = flow {
        val favoriteEntities = favoriteMovieDao.getAllFavorites()
        val favoriteMovies = favoriteEntities.mapNotNull { entity ->
            try {
                apiService.getMovieById(entity.movieId)
            } catch (e: Exception) {
                null
            }
        }
        emit(favoriteMovies)
    }
}
