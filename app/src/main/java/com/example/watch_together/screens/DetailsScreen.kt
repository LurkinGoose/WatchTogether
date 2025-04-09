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
    viewModel: MoviesViewModel,
    onDismiss: () -> Unit
) {
    // Загружаем данные при первом появлении экрана
    LaunchedEffect(movieId) {
        viewModel.getMovieDetails(movieId)
    }

    // Подписка на значения напрямую
    val isLoading = viewModel.isLoading
    val error = viewModel.error
    val movie = viewModel.movieDetails

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            error != null -> {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            movie != null -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Box {
                            AsyncImage(
                                model = movie.fullPosterPath,
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
                            text = movie.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = movie.overview,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
