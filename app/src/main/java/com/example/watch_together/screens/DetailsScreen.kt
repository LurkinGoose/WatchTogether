package com.example.watch_together.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.watch_together.viewModels.MoviesViewModel

@Composable
fun DetailsScreen(
    movieId: Int,
    moviesViewModel: MoviesViewModel,
    onDismiss: () -> Unit
) {
    // Загружаем данные при первом появлении экрана
    LaunchedEffect(movieId) {
        moviesViewModel.getMovieDetails(movieId)
    }

    // Получаем состояние из ViewModel
    val state by moviesViewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            state.error != null -> {
                Text(
                    text = "Ошибка: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.movieDetails != null -> {
                val movie = state.movieDetails
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Box {
                            AsyncImage(
                                model = movie?.fullPosterPath,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(600.dp),
                                contentScale = ContentScale.Crop
                            )

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Назад",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = movie?.title ?: "Название не доступно",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = movie?.overview ?: "Описание не доступно",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
