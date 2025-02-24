package com.example.watch_together

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.*
import com.example.watch_together.movieApiService.RetrofitInstance
import com.example.watch_together.repository.MovieRepository
import com.example.watch_together.screens.AuthScreen
import com.example.watch_together.screens.MainScreen
import com.example.watch_together.ui.theme.Watch_TogetherTheme
import com.example.watch_together.viewModels.*
import com.google.android.gms.auth.api.signin.GoogleSignIn

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val movieApiService by lazy { RetrofitInstance.api }
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val favoriteMovieDao by lazy { database.favoriteMovieDao() }
    private val movieRepository by lazy { MovieRepository(movieApiService, favoriteMovieDao, applicationContext) }
    private val movieViewModel: MovieViewModel by viewModels { MovieViewModelFactory(movieRepository) }
    private val favoritesViewModel: FavoritesViewModel by viewModels { FavoritesViewModelFactory(movieRepository) }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            task.addOnSuccessListener { account ->
                Log.d("MainActivity", "Google Sign-In Success: ${account.email}")
                authViewModel.signInWithGoogle(account) { success, error ->
                    if (success) {
                        Log.d("MainActivity", "Firebase Auth Success")
                    } else {
                        Log.e("MainActivity", "Firebase Auth Failed: $error")
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("MainActivity", "Google Sign-In Failed", e)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Watch_Together)
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { splashViewModel.isLoading.value }

        setContent {
            Watch_TogetherTheme {
                val navController = rememberNavController()
                val authState by authViewModel.authState.collectAsState()
                val isLoading by splashViewModel.isLoading.collectAsState()

                val startDestination = when {
                    isLoading -> "splash"
                    authState == AuthState.Authenticated -> "main"
                    else -> "auth"
                }

                NavHost(navController, startDestination = startDestination) {
                    composable("splash") { /* Пустой экран, управляется SplashScreen API */ }
                    composable("auth") {
                        AuthScreen(authViewModel, googleSignInLauncher) {
                            navController.navigate("main") {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    }
                    composable("main") {
                        MainScreen(
                            movieViewModel = movieViewModel,
                            favoritesViewModel = favoritesViewModel,
                            authViewModel = authViewModel,
                            paddingValues = PaddingValues()
                        )
                    }
                }
            }
        }
    }
}
