package com.example.watch_together.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.watch_together.viewModels.AuthViewModel

@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
) {
    val authState by authViewModel.state.collectAsState()
    val userName = authState.user?.displayName

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Настройки",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Привет, $userName!",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { authViewModel.signOut() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Выйти из аккаунта")
        }
    }
}
