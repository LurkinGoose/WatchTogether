package com.example.watch_together.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.watch_together.movieCards.MovieListItem
import com.example.watch_together.viewModels.FavoritesViewModel
import com.example.watch_together.viewModels.MovieViewModel

@Composable
fun FavoritesScreen(
    navController: NavController,
    movieViewModel: MovieViewModel,
    favoritesViewModel: FavoritesViewModel
) {
    val uiState by favoritesViewModel.uiState.collectAsState()

    // Запоминаем favoriteMovies, чтобы экран не перерисовывался без причины
    val favoriteMovies = remember(uiState.favoriteMovies) { uiState.favoriteMovies }

    LaunchedEffect(favoriteMovies) {
        Log.d("FavoritesScreen", "favoriteMovies обновились: ${favoriteMovies.size} фильмов")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .statusBarsPadding()
    ) {
        Text(
            text = "Избранное",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {
            when {
                uiState.loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                favoriteMovies.isEmpty() -> {
                    Text(
                        text = "Список избранных фильмов пуст",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(
                        state = movieViewModel.saveListState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(favoriteMovies, key = { movie -> movie.id }) { movie ->
                            MovieListItem(movie, favoritesViewModel, true) {
                                navController.navigate("movie_details/${movie.id}")
                            }
                        }
                    }
                }
            }
        }
    }
}
