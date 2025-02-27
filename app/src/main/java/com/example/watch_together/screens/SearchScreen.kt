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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.watch_together.movieCards.MovieListItem
import com.example.watch_together.viewModels.FavoritesViewModel
import com.example.watch_together.viewModels.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController,
                 movieViewModel: MovieViewModel,
                 favoritesViewModel: FavoritesViewModel) {


    val uiState by movieViewModel.uiState.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }

    Log.d("SearchScreen", "🔄 SearchScreen запустился")
    Log.d("SearchScreen", "📊 uiState обновился: loading=${uiState.loading}, movies=${uiState.movies.size}, error=${uiState.errorMessage}")

    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Введите название фильма") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                Log.d("SearchScreen", "🔍 Запускаем поиск: $query")
                movieViewModel.searchMovies(query)

            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Поиск")
        }

        if (uiState.loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        uiState.errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn {
            items(uiState.movies) { movie ->
                MovieListItem(movie, favoritesViewModel) {
                    navController.navigate("movieDemo/${movie.id}")
                }
            }
        }
    }
}
