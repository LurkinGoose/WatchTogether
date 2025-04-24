package com.example.watch_together.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.watch_together.BuildConfig
import com.example.watch_together.movieCards.MovieListItem
import com.example.watch_together.viewModels.MoviesViewModel

@Composable
fun SearchScreen(
    navController: NavController,
    moviesViewModel: MoviesViewModel
) {
    var query by rememberSaveable { mutableStateOf("") }

    val state by moviesViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        moviesViewModel.loadFavorites()
        moviesViewModel.loadTopRatedMovies()
    }

    Log.d("TMDB_API_KEY", BuildConfig.TMDB_API_KEY)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {

        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Введите название фильма") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { moviesViewModel.searchMovies(query) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Поиск")
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.movies.isEmpty() -> {
                    Text(
                        text = "Нет результатов для поиска",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(state = state.searchListState) {
                        items(
                            state.movies,
                            key = { it.id }
                        ) { movie ->
                            val isFavorite = state.favorites.any { it.id == movie.id }
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

            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = { moviesViewModel.clearError() }) {
                            Text("Ок")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}
