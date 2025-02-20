package com.example.watch_together

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Настройки", style = MaterialTheme.typography.headlineLarge)
    }
}
