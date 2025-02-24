package com.example.watch_together.models

enum class Screen(val route: String) {
    Search("search"),
    Favorites("favorites"),
    Settings("settings"),
    MovieDemo("movie_demo/{movieId}");

    fun withArgs(arg: String): String {
        return route.replace("{movieId}", arg)
    }

}
