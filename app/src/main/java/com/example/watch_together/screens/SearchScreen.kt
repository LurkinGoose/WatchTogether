package com.example.watch_together.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.watch_together.movieCards.MovieListItem
import com.example.watch_together.viewModels.FavoritesViewModel
import com.example.watch_together.viewModels.MovieViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    movieViewModel: MovieViewModel,
    favoritesViewModel: FavoritesViewModel,
    onMovieSelected: (Int) -> Unit,
    paddingValues: PaddingValues
) {
    var query by remember { mutableStateOf("") }
    val movies by movieViewModel.movieList.collectAsState()
    val loading by movieViewModel.loading.collectAsState()
    val errorMessage by movieViewModel.errorMessage.collectAsState()

    val listState = movieViewModel.saveListState

    Column(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Введите название фильма") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { movieViewModel.searchMovies(query) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Поиск")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            itemsIndexed(movies) { index, movie ->
                MovieListItem(movie, favoritesViewModel) {
                    onMovieSelected(movie.id)
                }
                Spacer(
                    modifier = Modifier.height(
                        if (index == movies.lastIndex) paddingValues.calculateBottomPadding()
                        else 1.dp
                    )
                )
            }
        }
    }
}
