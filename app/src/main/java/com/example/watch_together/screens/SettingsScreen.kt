package com.example.watch_together.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.watch_together.viewModels.AuthViewModel
import com.example.watch_together.viewModels.MovieViewModel

@Composable
fun SettingsScreen(movieViewModel: MovieViewModel, authViewModel: AuthViewModel, paddingValues: PaddingValues) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)
    ) {
        Text(text = "Настройки", style = MaterialTheme.typography.headlineLarge)

        Button(
            onClick = { authViewModel.signOut(context, movieViewModel) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Выйти из аккаунта")
        }
    }
}

