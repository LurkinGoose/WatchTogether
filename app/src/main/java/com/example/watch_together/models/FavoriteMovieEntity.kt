package com.example.watch_together.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteMovieEntity(
    @PrimaryKey val movieId: Int,
    val addedAt: Long = System.currentTimeMillis()
)
