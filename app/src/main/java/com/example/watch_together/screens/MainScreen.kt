//package com.example.watch_together.screens
//
//import android.util.Log
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Scaffold
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavController
//import androidx.navigation.compose.NavHost
//import com.example.watch_together.models.Screen
//import com.example.watch_together.tabHost.BottomNavigationBar
//import com.example.watch_together.viewModels.AuthViewModel
//import com.example.watch_together.viewModels.FavoritesViewModel
//import com.example.watch_together.viewModels.MovieViewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MainScreen(
//    navController: NavController,
//    movieViewModel: MovieViewModel,
//    favoritesViewModel: FavoritesViewModel,
//    authViewModel: AuthViewModel
//) {
//    Log.d("MainScreen", "MainScreen запустился")
//
//    Scaffold(
//        bottomBar = {
//            BottomNavigationBar(
//                selectedScreen = Screen.fromRoute(navController.currentDestination?.route),
//                onScreenSelected = { screen ->
//                    val currentRoute = navController.currentDestination?.route
//                    if (currentRoute != screen.route) { // ✅ Только если экран реально меняется
//                        Log.d("BottomNavigationBar", "Переход на экран: ${screen.route}")
//                        navController.navigate(screen.route) {
//                            popUpTo(Screen.Search.route) {
//                                inclusive = false
//                                saveState = true
//                            }
//                            restoreState = true
//                        }
//                    }
//                },
//                favoritesViewModel = favoritesViewModel
//            )
//        }
//    ) { paddingValues ->
//        NavHost(
//            navController = navController,
//            startDestination = Screen.Search.route,
//            modifier = Modifier.padding(paddingValues)
//        ) {
//            composable(Screen.Search.route) {
//                Log.d("Navigation", "Перешли на экран Search")
//                SearchScreen(movieViewModel, favoritesViewModel, { movieId ->
//                    navController.navigate("movie_demo/$movieId")
//                }, paddingValues)
//            }
//            composable(Screen.Favorites.route) {
//                Log.d("Navigation", "Перешли на экран Favorites")
//                FavoritesScreen(movieViewModel, favoritesViewModel, { movieId ->
//                    navController.navigate("movie_demo/$movieId")
//                }, paddingValues)
//            }
//            composable(Screen.Settings.route) {
//                Log.d("Navigation", "Перешли на экран Settings")
//                SettingsScreen(movieViewModel, authViewModel, paddingValues)
//            }
//        }
//    }
//}
