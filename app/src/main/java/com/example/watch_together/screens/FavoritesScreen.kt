package com.example.watch_together.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.watch_together.movieCards.MovieListItem
import com.example.watch_together.viewModels.FavoritesViewModel
import com.example.watch_together.viewModels.MovieViewModel

@Composable
fun FavoritesScreen(
    movieViewModel: MovieViewModel,
    favoritesViewModel: FavoritesViewModel,
    onMovieSelected: (Int) -> Unit,
    paddingValues: PaddingValues
){
    val favoriteMovies by favoritesViewModel.favoriteMovies.collectAsState()
    val listState = movieViewModel.saveListState

    LaunchedEffect(Unit) {
        favoritesViewModel.loadFavoriteMovies()
    }

    Column(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Text(
            text = "Избранное",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )

        if (favoriteMovies.isEmpty()) {
            Text(
                text = "Список избранных фильмов пуст",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            ) {
                itemsIndexed(favoriteMovies) { index, movie ->
                    MovieListItem(movie, favoritesViewModel) {
                        onMovieSelected(movie.id)
                    }
                    Spacer(
                        modifier = Modifier.height(
                            if (index == favoriteMovies.lastIndex) paddingValues.calculateBottomPadding()
                            else 1.dp
                        )
                    )
                }
            }
        }
    }
}
