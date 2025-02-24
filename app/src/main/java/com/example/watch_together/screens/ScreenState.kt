package com.example.watch_together.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.watch_together.viewModels.MovieViewModel


@Composable
fun ScreenState(viewModel: MovieViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            viewModel.loading.collectAsState().value -> CircularProgressIndicator()
            viewModel.errorMessage.collectAsState().value != null -> Text(
                text = viewModel.errorMessage.collectAsState().value!!,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}