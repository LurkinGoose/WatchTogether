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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.watch_together.movieCards.MovieListItem
import com.example.watch_together.viewModels.FavoritesViewModel
import com.example.watch_together.viewModels.MovieViewModel

@Composable
fun FavoritesScreen(navController: NavController,
                    movieViewModel: MovieViewModel,
                    favoritesViewModel: FavoritesViewModel) {


    val uiState by favoritesViewModel.uiState.collectAsState()
    val paddingValues = PaddingValues(0.dp)
    Log.d("FavoriteScreen", "FavoriteScreen запустился")
    Log.d("FavoritesScreen", "🔄 uiState: loading=${uiState.loading}, favorites=${uiState.favoriteMovies.size}")


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding()
            .background(Color.Transparent)
            .statusBarsPadding()
    ) {
        Text(
            text = "Избранное",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.favoriteMovies.isEmpty() -> {
                    Text(
                        text = "Список избранных фильмов пуст",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(
                        state = movieViewModel.saveListState,
                        modifier = Modifier.fillMaxSize().background(Color.LightGray)
                    ) {
                        itemsIndexed(uiState.favoriteMovies) { index, movie ->
                            MovieListItem(movie, favoritesViewModel) {
                                navController.navigate("movieDemo/${movie.id}")
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
            }
        }
    }
}
