package com.example.watch_together.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.watch_together.movieCards.MovieListItem
import com.example.watch_together.viewModels.MoviesViewModel

@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: MoviesViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Text("Избранное", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                viewModel.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                viewModel.favorites.isEmpty() -> {
                    Text(
                        text = "Список избранных фильмов пуст",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(state = viewModel.saveListState) {
                        items(viewModel.favorites, key = { it.id }) { movie ->
                            MovieListItem(movie, viewModel, true) {
                                navController.navigate("movie_details/${movie.id}")
                            }
                        }
                    }
                }
            }
        }
    }
}
