package com.example.watch_together.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies", primaryKeys = ["mediaId", "mediaType"])
data class FavoriteMovieEntity(
    val mediaId: Int,
    val mediaType: String, // "movie" или "tv"
    val addedAt: Long = System.currentTimeMillis()
)

