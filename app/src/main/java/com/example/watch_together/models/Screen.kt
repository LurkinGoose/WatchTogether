package com.example.watch_together.models

sealed class Screen(val route: String, val label: String) {
    object Search : Screen("search", "Поиск")
    object Favorites : Screen("favorites", "Избранное")
    object Settings : Screen("settings", "Настройки")
    object MovieDemo : Screen("movieDemo", "Демо")

}
