package com.example.watch_together

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: MovieViewModel, paddingValues: PaddingValues) {
    var query by remember { mutableStateOf("") }
    val movies by viewModel.movieList.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val listState = viewModel.searchListState

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Введите название фильма") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.searchMovies(query) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Поиск")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Показ индикатора загрузки
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        // Показ сообщения об ошибке
        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(
                Alignment.CenterHorizontally))
        }

        // Показ списка фильмов
        LazyColumn(state = listState,
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(movies) { moviesList ->
                MovieItem(moviesList)
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}