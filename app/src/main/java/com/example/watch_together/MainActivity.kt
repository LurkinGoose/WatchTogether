package com.example.watch_together

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.example.watch_together.navigation.BottomNavigationBar
import com.example.watch_together.screens.*
import com.example.watch_together.ui.theme.Watch_TogetherTheme
import com.example.watch_together.viewModels.*
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Watch_Together)
        val splashScreen = installSplashScreen()

        setContent {
            Watch_TogetherTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel()
                val authState by authViewModel.state.collectAsState()
                val moviesViewModel: MoviesViewModel = hiltViewModel()

                LaunchedEffect(authState.isLoading) {
                    splashScreen.setKeepOnScreenCondition { authState.isLoading }
                }

                val startDestination = if (authState.user != null) "search" else "signIn"

                Scaffold(
                    bottomBar = {
                        if (authState.user != null) {
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
                        composable("signIn") {
                            SignInScreen(navController, authViewModel) {
                                navController.navigate("search") {
                                    popUpTo("signIn") { inclusive = true }
                                }
                            }
                        }

                        composable("signUp") {
                            SignUpScreen(navController, authViewModel) {
                                navController.navigate("search")
                            }
                        }

                        composable("search") {
                            SearchScreen(navController, moviesViewModel)
                        }

                        composable("favorites") {
                            FavoritesScreen(navController, moviesViewModel)
                        }

                        composable("settings") {
                            SettingsScreen(authViewModel)
                        }

                        composable("movie_details/{movieId}"
//                            enterTransition = {
//                                slideIntoContainer(
//                                    AnimatedContentTransitionScope.SlideDirection.Left,
//                                    animationSpec = tween(700)
//                                )
//                            },
//                            exitTransition = {
//                                slideOutOfContainer(
//                                    AnimatedContentTransitionScope.SlideDirection.Right,
//                                    animationSpec = tween(700)
//                                )
//                            }
                        ) { backStackEntry ->
                            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                            movieId?.let {
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
