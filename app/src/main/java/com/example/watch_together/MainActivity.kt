package com.example.watch_together

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.example.watch_together.models.Screen
import com.example.watch_together.navigation.BottomNavigationBar
import com.example.watch_together.screens.*
import com.example.watch_together.ui.theme.Watch_TogetherTheme
import com.example.watch_together.viewModels.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            task.addOnSuccessListener { account ->
                authViewModel.signInWithGoogle(account) { success, error ->
                    if (success) {
                        Log.d("AUTH", "Пользователь вошел: ${account.email}")
                    } else {
                        Log.e("AUTH", "Ошибка входа: $error")
                    }
                }
            }.addOnFailureListener {
                Log.e("AUTH", "Google Sign-In не удался", it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Watch_Together)
        installSplashScreen().setKeepOnScreenCondition { false }

        setContent {
            Watch_TogetherTheme {
                val navController = rememberNavController()
                val user by authViewModel.user.collectAsState()
                val moviesViewModel: MoviesViewModel = hiltViewModel()

                val startDestination = if (user != null) "search" else "auth"

                Scaffold(
                    bottomBar = {
                        if (user != null) {
                            BottomNavigationBar(
                                modifier = Modifier.fillMaxWidth(),
                                navController = navController,
                                currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: "search",
                                moviesViewModel = moviesViewModel
                            )
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
                                navController.navigate("search") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        }

                        composable("search") {
//                            Log.d("SCREEN", "Открыт экран: Search")
                            SearchScreen(navController, moviesViewModel)
                        }

                        composable("favorites") {
//                            Log.d("SCREEN", "Открыт экран: Favorites")
                            FavoritesScreen(navController, moviesViewModel)
                        }

                        composable("settings") {
//                            Log.d("SCREEN", "Открыт экран: Settings")
                            SettingsScreen(moviesViewModel, authViewModel, paddingValues)
                        }

                        composable("movie_details/{movieId}",

                            enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(700)
                            )
                        },
                            exitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(700)
                                )
                            }) { backStackEntry ->
                            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                            movieId?.let {
//                                Log.d("SCREEN", "Открыт экран: Details для movieId=$it")
                                DetailsScreen(it, moviesViewModel) {
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
