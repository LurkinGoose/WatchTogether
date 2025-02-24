package com.example.watch_together.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.watch_together.viewModels.MovieViewModel


@Composable
fun DemoScreen(
    movieId: Int,
    movieViewModel: MovieViewModel,
    onDismiss: () -> Unit, // Просто вызываем переданную функцию
    paddingValues: PaddingValues
) {
    val movieDetails by movieViewModel.movieDetails.collectAsState()
    val loading by movieViewModel.loading.collectAsState()
    val errorMessage by movieViewModel.errorMessage.collectAsState()

    // Загружаем данные только если movieId изменился
    LaunchedEffect(movieId) {
        movieViewModel.getMovieDetails(movieId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (loading) {
            // Показываем индикатор загрузки
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Показываем данные только если загрузка завершена
            if (movieDetails != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = paddingValues
                ) {
                    item {
                        Box {
                            AsyncImage(
                                model = movieDetails?.fullPosterPath,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(600.dp),
                                contentScale = ContentScale.Crop
                            )

                            IconButton(
                                onClick = {
                                    // Навигация на экран избранного
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Назад",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = movieDetails?.title ?: "Загрузка...",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = movieDetails?.overview ?: "Описание не доступно",
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Показываем ошибку, если она есть
            if (errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
