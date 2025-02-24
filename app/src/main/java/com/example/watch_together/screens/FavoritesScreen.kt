package com.example.watch_together.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.watch_together.movieCards.MovieListItem
import com.example.watch_together.viewModels.FavoritesViewModel
import com.example.watch_together.viewModels.MovieViewModel

@Composable
fun FavoritesScreen(
    movieViewModel: MovieViewModel,
    favoritesViewModel: FavoritesViewModel,
    onMovieSelected: (Int) -> Unit,
    paddingValues: PaddingValues
) {
    val uiState by favoritesViewModel.uiState.collectAsState() // ✅ Теперь используем uiState
    Log.d("AppLog", "FavoriteScreen запустился")

    LaunchedEffect(Unit) {
        favoritesViewModel.loadFavorites()
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

        if (uiState.loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally)) // ✅ Показываем индикатор загрузки
        } else if (uiState.favoriteMovies.isEmpty()) {
            Text(
                text = "Список избранных фильмов пуст",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                state = movieViewModel.saveListState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            ) {
                itemsIndexed(uiState.favoriteMovies) { index, movie -> // ✅ Теперь берём из uiState
                    MovieListItem(movie, favoritesViewModel) {
                        onMovieSelected(movie.id)
                    }
                    Spacer(
                        modifier = Modifier.height(
                            if (index == uiState.favoriteMovies.lastIndex) paddingValues.calculateBottomPadding()
                            else 1.dp
                        )
                    )
                }
            }
        }

        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
