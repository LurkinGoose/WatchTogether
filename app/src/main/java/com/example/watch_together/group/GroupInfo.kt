package com.example.watch_together.group

data class Group(
    val groupId: String = "",
    val groupName: String = "",
    val createdBy: String = "",
    val groupMembers: Map<String, Boolean> = emptyMap(),
    val groupMedia: Map<String, MediaAddedBy> = emptyMap() // ключ: movie_123 или tv_456
)

data class MediaAddedBy(
    val addedBy: String = "",
    val mediaType: String = "" // "movie" или "tv"
)
