//package com.example.watch_together.tabHost
//
//import androidx.compose.animation.*
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.watch_together.screens.DemoScreen
//import com.example.watch_together.screens.FavoritesScreen
//import com.example.watch_together.viewModels.FavoritesViewModel
//import com.example.watch_together.viewModels.MovieViewModel
//import kotlinx.coroutines.delay
//
//@Composable
//fun FavoritesTabNavHost(
//    movieViewModel: MovieViewModel,
//    favoritesViewModel: FavoritesViewModel,
//    paddingValues: PaddingValues,
//    savedMovieId: Int?, // Добавляем сохраненный movieId
//    onMovieIdSaved: (Int?) -> Unit // Функция для сохранения movieId
//) {
//    val favoritesNavController = rememberNavController()
//    // Флаг завершения анимации
//    val isExitAnimationFinished = remember { mutableStateOf(false) }
//
//    NavHost(
//        navController = favoritesNavController,
//        startDestination = if (savedMovieId != null) "demo" else "favorites"
//    ) {
//        composable(
//            "favorites",
//            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(2000)) },
//            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(2000)) }
//        ) {
//            FavoritesScreen(
//                movieViewModel = movieViewModel,
//                favoritesViewModel = favoritesViewModel,
//                onMovieSelected = { movieId ->
//                    onMovieIdSaved(movieId)
//                    favoritesNavController.navigate("demo")
//                },
//                paddingValues = paddingValues
//            )
//        }
//
//        composable(
//            "demo",
//            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(1000)) },
//            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(1000)) }
//        ) {
//            savedMovieId?.let { movieId ->
//                DemoScreen(
//                    movieId = movieId,
//                    movieViewModel = movieViewModel,
//                    onDismiss = {
//                        onMovieIdSaved(null)
//                        favoritesNavController.popBackStack()
//                        isExitAnimationFinished.value = true
//                    },
//                    paddingValues = paddingValues
//                )
//            }
//        }
//    }
//
//    LaunchedEffect(isExitAnimationFinished.value) {
//        if (isExitAnimationFinished.value) {
//            delay(1000)
//            movieViewModel.clearMovieDetails()
//        }
//    }
//}
