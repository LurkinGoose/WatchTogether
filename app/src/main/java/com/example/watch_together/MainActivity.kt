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
import androidx.compose.runtime.saveable.rememberSaveable
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

    private val splashViewModel: SplashViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val movieApiService by lazy { RetrofitInstance.api }
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val favoriteMovieDao by lazy { database.favoriteMovieDao() }
    private val movieRepository by lazy {
        MovieRepository(
            movieApiService,
            favoriteMovieDao,
            applicationContext
        )
    }
    private val movieViewModel: MovieViewModel by viewModels { MovieViewModelFactory(movieRepository) }
    private val favoritesViewModel: FavoritesViewModel by viewModels {
        FavoritesViewModelFactory(movieRepository)
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            task.addOnSuccessListener { account ->
                Log.d("MainActivity", "Google Sign-In Success: ${account.email}")
                authViewModel.signInWithGoogle(account) { success, error ->
                    if (!success) {
                        Log.e("MainActivity", "Firebase Auth Failed: $error")
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("MainActivity", "Google Sign-In Failed", e)
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Watch_Together)
        super.onCreate(savedInstanceState)

        Log.d("MainActivity", "onCreate() запустился")

        enableEdgeToEdge()

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { splashViewModel.isLoading.value }

        setContent {
            Watch_TogetherTheme {
                val navController = rememberNavController()
                val authState by authViewModel.authState.collectAsState()
                val isLoading by splashViewModel.isLoading.collectAsState()

                val currentAuthState = rememberUpdatedState(authState)

                Log.d("Navigation", "Загрузка: $isLoading, Авторизация: $authState")

                Scaffold(
                    bottomBar = {
                        if (authState == AuthState.Authenticated) {
                            val currentRoute = navController.currentDestination?.route ?: Screen.Search.route
                            BottomNavigationBar(
                                selectedScreen = Screen.fromRoute(currentRoute),
                                onScreenSelected = { screen ->
                                    if (currentRoute != screen.route) {
                                        Log.d("BottomNavigationBar", "Переход на экран: ${screen.route}")
                                        navController.navigate(screen.route) {
                                            popUpTo(Screen.Search.route) { inclusive = false; saveState = true }
                                            restoreState = true
                                        }
                                    }
                                },
                                favoritesViewModel = favoritesViewModel
                            )
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable("splash") {
                            Log.d("Navigation", "Перешли на экран Splash")

                            var isNavigated by rememberSaveable { mutableStateOf(false) }

                            LaunchedEffect(Unit) { // Выполняется только 1 раз
                                Log.d("Navigation", "Ожидание окончания загрузки...")

                                splashViewModel.isLoading.collect { loading ->
                                    if (!loading && !isNavigated) {
                                        isNavigated = true

                                        val target = if (authViewModel.authState.value == AuthState.Authenticated) {
                                            Screen.Search.route
                                        } else {
                                            "auth"
                                        }

                                        Log.d("Navigation", "🔄 Переход с Splash на $target")
                                        navController.navigate(target) {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    }
                                }
                            }
                        }

                        composable("auth") {
                            AuthScreen(authViewModel, googleSignInLauncher) {
                                navController.navigate(Screen.Search.route) {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        }
                        composable(Screen.Search.route) {
                            Log.d("Navigation", "Перешли на экран Search")
                            SearchScreen(movieViewModel, favoritesViewModel, { movieId ->
                                navController.navigate("movie_demo/$movieId")
                            }, paddingValues)
                        }
                        composable(Screen.Favorites.route) {
                            Log.d("Navigation", "Перешли на экран Favorites")
                            FavoritesScreen(movieViewModel, favoritesViewModel, { movieId ->
                                navController.navigate("movie_demo/$movieId")
                            }, paddingValues)
                        }
                        composable(Screen.Settings.route) {
                            Log.d("Navigation", "Перешли на экран Settings")
                            SettingsScreen(movieViewModel, authViewModel, paddingValues)
                        }
                        composable(Screen.MovieDemo.route) { backStackEntry ->
                            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                            movieId?.let {
                                DemoScreen(it, movieViewModel, { navController.popBackStack() }, paddingValues)
                            }
                        }
                    }
                }
            }
        }
    }
}
