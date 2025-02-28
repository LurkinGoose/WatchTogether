package com.example.watch_together.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Search : Screen("search", "Поиск", Icons.Filled.Search)
    object Favorites : Screen("favorites", "Избранное", Icons.Filled.Favorite)
    object Settings : Screen("settings", "Настройки", Icons.Filled.Settings)
    object MovieDemo : Screen("movie_cart", "Карточка фильма", Icons.Filled.Search) // добавь сюда иконку по необходимости
}