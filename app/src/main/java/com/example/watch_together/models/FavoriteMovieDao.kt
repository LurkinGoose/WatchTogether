package com.example.watch_together.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteMovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(movie: FavoriteMovieEntity)

    @Delete
    suspend fun removeFavorite(movie: FavoriteMovieEntity)

    @Query("SELECT * FROM favorite_movies ORDER BY addedAt DESC")
    suspend fun getAllFavorites(): List<FavoriteMovieEntity>
}