package com.example.watch_together

import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn

@Composable
fun AuthScreen(viewModel: AuthViewModel, googleSignInLauncher: ActivityResultLauncher<Intent>, onAuthSuccess: () -> Unit) {
    val user by viewModel.user.collectAsState()
    val context = LocalContext.current
    val googleSignInClient = viewModel.getGoogleSignInClient(context)

    LaunchedEffect(user) {
        Log.d("AuthScreen", "Auth state changed: $user")
        if (user != null) {
            Log.d("AuthScreen", "User authenticated, navigating to main screen")
            onAuthSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) }) {
            Text("Войти через Google")
        }
    }
}
