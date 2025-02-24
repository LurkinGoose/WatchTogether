package com.example.watch_together.models

sealed class Screen(val route: String) {
    object Search : Screen("search")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
    object MovieDemo : Screen("movieDemo")

    companion object {
        fun fromRoute(route: String?): Screen {
            return when (route) {
                Favorites.route -> Favorites
                Settings.route -> Settings
                else -> Search
            }
        }
    }
}
