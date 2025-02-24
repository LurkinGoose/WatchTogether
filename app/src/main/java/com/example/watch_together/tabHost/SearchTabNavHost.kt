package com.example.watch_together.tabHost

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.watch_together.screens.DemoScreen
import com.example.watch_together.screens.SearchScreen
import com.example.watch_together.viewModels.FavoritesViewModel
import com.example.watch_together.viewModels.MovieViewModel

@Composable
fun SearchTabNavHost(
    movieViewModel: MovieViewModel,
    favoritesViewModel: FavoritesViewModel,
    paddingValues: PaddingValues
) {
    val searchNavController = rememberNavController() // Вложенный навигатор

    NavHost(
        navController = searchNavController,
        startDestination = "search"
    ) {
        composable("search") {
            SearchScreen(movieViewModel, favoritesViewModel,
                onMovieSelected = { movieId ->
                    searchNavController.navigate("movie_demo/$movieId")
                                  },
                paddingValues)
        }
        composable("movie_demo/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
            if (movieId != null) {
                DemoScreen(movieId, movieViewModel, onDismiss = { searchNavController.popBackStack() }, paddingValues)
            }
        }
    }
}

