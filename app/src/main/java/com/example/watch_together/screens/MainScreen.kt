package com.example.watch_together.screens

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.watch_together.models.Screen
import com.example.watch_together.viewModels.AuthViewModel
import com.example.watch_together.viewModels.FavoritesViewModel
import com.example.watch_together.viewModels.MovieViewModel

@Composable
fun MainScreen(
    movieViewModel: MovieViewModel,
    favoritesViewModel: FavoritesViewModel,
    authViewModel: AuthViewModel,
    paddingValues: PaddingValues
) {
    Log.d("MainScreen", "MainScreen recomposed")
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "search"
    ) {
        composable(Screen.Search.route) {
            SearchScreen(
                movieViewModel = movieViewModel,
                favoritesViewModel = favoritesViewModel,
                onMovieSelected = { movieId ->
                    navController.navigate("movie_demo/$movieId")
                },
                paddingValues = paddingValues
            )
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                movieViewModel = movieViewModel,
                favoritesViewModel = favoritesViewModel,
                onMovieSelected = { movieId ->
                    navController.navigate("movie_demo/$movieId")
                },
                paddingValues = paddingValues
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                authViewModel = authViewModel,
                movieViewModel = movieViewModel,
                paddingValues = paddingValues
            )
        }
        composable(Screen.MovieDemo.route) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
            movieId?.let {
                DemoScreen(
                    movieId = it,
                    movieViewModel = movieViewModel,
                    onDismiss = {
                        navController.popBackStack()
                    },
                    paddingValues = paddingValues
                )
            }
        }
    }
}
