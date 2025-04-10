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
    moviesViewModel: MoviesViewModel
) {
    var query by rememberSaveable { mutableStateOf("") }

    val listState = moviesViewModel.searchListState

    LaunchedEffect(Unit) {
        moviesViewModel.loadFavorites()
        moviesViewModel.loadTopRatedMovies()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()) {

        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Введите название фильма") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { moviesViewModel.searchMovies(query) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Поиск")
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                moviesViewModel.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                moviesViewModel.movies.isEmpty() -> {
                    Text(
                        text = "Нет результатов для поиска",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(state = listState) {
                        items(
                            moviesViewModel.movies,
                            key = { it.id }
                        ) { movie ->
                            val isFavorite = moviesViewModel.favorites.any { it.id == movie.id }
                            MovieListItem(
                                movie = movie,
                                favoritesViewModel = moviesViewModel,
                                isFavorite = isFavorite,
                                onClick = {
                                    navController.navigate("movie_details/${movie.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
