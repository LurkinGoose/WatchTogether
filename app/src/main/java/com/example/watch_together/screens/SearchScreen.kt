package com.example.watch_together.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.watch_together.movieCards.MovieListItem
import com.example.watch_together.viewModels.MoviesViewModel

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: MoviesViewModel
) {
    var query by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Введите название фильма") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.searchMovies(query) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Поиск")
        }

        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        viewModel.error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn {
            items(viewModel.movies, key = { it.id }) { movie ->
                val isFavorite = viewModel.favorites.any { it.id == movie.id }
                MovieListItem(movie, viewModel, isFavorite) {
                    navController.navigate("movie_details/${movie.id}")
                }
            }
        }
    }
}
