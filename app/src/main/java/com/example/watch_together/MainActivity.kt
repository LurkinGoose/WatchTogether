package com.example.watch_together

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.*
import com.example.watch_together.models.Screen
import com.example.watch_together.movieApiService.RetrofitInstance
import com.example.watch_together.repository.MovieRepository
import com.example.watch_together.screens.*
import com.example.watch_together.tabHost.BottomNavigationBar
import com.example.watch_together.ui.theme.Watch_TogetherTheme
import com.example.watch_together.viewModels.*
import com.google.android.gms.auth.api.signin.GoogleSignIn

class MainActivity : ComponentActivity() {

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
                    if (!success) Log.e("MainActivity", "Firebase Auth Failed: $error")
                }
            }.addOnFailureListener { e ->
                Log.e("MainActivity", "Google Sign-In Failed", e)
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Watch_Together)
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        installSplashScreen().setKeepOnScreenCondition { false }

        setContent {
            Watch_TogetherTheme {
                val navController = rememberNavController()
                val authState by authViewModel.authState.collectAsState()
                val startDestination = if (authState == AuthState.Authenticated) Screen.Search.route else "auth"

                Scaffold(
                    bottomBar = {
                        if (authState == AuthState.Authenticated) {
                            BottomNavigationBar(navController)
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable("auth") {
                            AuthScreen(authViewModel, googleSignInLauncher) {
                                navController.navigate(Screen.Search.route) {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        }
                        composable(Screen.Search.route) {
                            SearchScreen(movieViewModel, favoritesViewModel) { movieId ->
                                navController.navigate("movie_demo/$movieId")
                            }
                        }
                        composable(Screen.Favorites.route) {
                            FavoritesScreen(movieViewModel, favoritesViewModel) { movieId ->
                                navController.navigate("movie_demo/$movieId")
                            }
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(movieViewModel, authViewModel)
                        }
                        composable("movie_demo/{movieId}") { backStackEntry ->
                            backStackEntry.arguments?.getString("movieId")?.toIntOrNull()?.let { movieId ->
                                DemoScreen(movieId, movieViewModel) { navController.popBackStack() }
                            }
                        }
                    }
                }
            }
        }
    }
}
