package com.example.watch_together.screens

import android.util.Log
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
import com.example.watch_together.viewModels.FavoritesViewModel
import com.example.watch_together.viewModels.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    movieViewModel: MovieViewModel,
    favoritesViewModel: FavoritesViewModel,
    paddingValues: PaddingValues
) {
    Log.d("SearchScreen", "🔄 SearchScreen пересоздался")

    val uiState by movieViewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }

    Log.d("SearchScreen", "📊 uiState обновился: loading=${uiState.loading}, movies=${uiState.movies.size}, error=${uiState.errorMessage}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Введите название фильма") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                Log.d("SearchScreen", "🔍 Запускаем поиск: $query")
                movieViewModel.searchMovies(query)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Поиск")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.loading) {
            Log.d("SearchScreen", "⏳ Идет загрузка...")
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        uiState.errorMessage?.let {
            Log.d("SearchScreen", "❌ Ошибка: $it")
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        LazyColumn(
            state = movieViewModel.saveListState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(uiState.movies) { movie ->
                Log.d("SearchScreen", "🎬 Добавляем фильм в список: ${movie.title} (ID: ${movie.id})")
                MovieListItem(movie, favoritesViewModel) {
                    Log.d("SearchScreen", "🎥 Выбран фильм: ${movie.title} (ID: ${movie.id})")
                    navController.navigate("movieDemo")

                }
            }
        }
    }
}
