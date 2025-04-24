package com.example.watch_together.screens


import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
    moviesViewModel: MoviesViewModel
) {
    val state by moviesViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        moviesViewModel.loadFavorites()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Text(
            "Избранное",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.favorites.isEmpty() -> {
                    Text(
                        text = "Список избранных фильмов пуст",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(state = state.favoritesListState) {
                        items(
                            items = state.favorites,
                            key = { it.id }
                        ) { movie ->
                            MovieListItem(
                                movie = movie,
                                favoritesViewModel = moviesViewModel,
                                isFavorite = true,
                                onClick = {
                                    navController.navigate("movie_details/${movie.id}")
                                },
                                modifier = Modifier.animateItem(
                                    placementSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                )
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
