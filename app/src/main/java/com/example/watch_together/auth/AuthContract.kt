package com.example.watch_together.auth

import android.content.Context
import com.google.firebase.auth.FirebaseUser

sealed class AuthIntent {
    data class SignInWithEmail(val email: String, val password: String) : AuthIntent()
    data class SignUpWithEmail(val fullName: String, val email: String, val password: String) : AuthIntent()
    data class SignInWithGoogle(val context: Context) : AuthIntent()
    data object SignOut : AuthIntent()
//    data object LinkToGoogle: AuthIntent()
    data object ClearError : AuthIntent()
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null,
)
