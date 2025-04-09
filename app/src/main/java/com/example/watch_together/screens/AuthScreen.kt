package com.example.watch_together.screens

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.watch_together.viewModels.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    googleSignInLauncher: ActivityResultLauncher<Intent>,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val googleSignInClient = remember { viewModel.getGoogleSignInClient(context) }
    val user by viewModel.user.collectAsState()

    LaunchedEffect(user) {
        if (user != null) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }) {
            Text("Войти с помощью Google")
        }
    }
}

