package com.example.watch_together

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.watch_together.navigation.BottomNavigationBar
import com.example.watch_together.screens.*
import com.example.watch_together.ui.theme.Watch_TogetherTheme
import com.example.watch_together.viewModels.*
import dagger.hilt.android.AndroidEntryPoint
import com.example.watch_together.auth.AuthViewModel
import com.example.watch_together.auth.SignInScreen
import com.example.watch_together.auth.SignUpScreen
import com.example.watch_together.auth.UserPreferences
import com.example.watch_together.group.GroupScreen
import com.example.watch_together.group.GroupViewModel
import com.example.watch_together.models.MediaType
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var groupViewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Watch_Together)
        val splashScreen = installSplashScreen()


        // ✅ Hilt совместимая инициализация ViewModel
        groupViewModel = ViewModelProvider(this, defaultViewModelProviderFactory)[GroupViewModel::class.java]

        // ✅ Инициализируем ViewModel до getDynamicLink
        groupViewModel = ViewModelProvider(this)[GroupViewModel::class.java]

        @Suppress("DEPRECATION")
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                val deepLink: Uri? = pendingDynamicLinkData?.link
                deepLink?.let { uri ->
                    val groupId = uri.getQueryParameter("groupId")
                    if (groupId != null) {
                        Log.d("DynamicLink", "✅ Получен groupId: $groupId")
                        groupViewModel.joinGroupFromInvite(groupId)  // ✅ вызов
                    }
                }
            }
            .addOnFailureListener(this) { e ->
                Log.e("DynamicLink", "❌ Ошибка получения ссылки: ${e.message}", e)
            }

        setContent {
            Watch_TogetherTheme {
                val navController = rememberNavController()

                val authViewModel: AuthViewModel = hiltViewModel()
                val authState by authViewModel.uiState.collectAsState()

                val mainViewModel: MainViewModel = hiltViewModel()
                val personViewModel: PersonViewModel = hiltViewModel()

                val userPreferences = UserPreferences(context = applicationContext)


                LaunchedEffect(authState.isLoading) {
                    splashScreen.setKeepOnScreenCondition { authState.isLoading }
                }

                val startDestination = if (authState.user != null) "search" else "signIn"
//                val startDestination = "details/1396/tv"
//                val startDestination = "details/58/movie"



                Scaffold(
                    bottomBar = {
                        if (authState.user != null) {
                            BottomNavigationBar(
                                navController = navController,
                                currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: "search",
                                mainViewModel = mainViewModel,
                                onResetScroll = {
                                    mainViewModel.triggerScrollToTop()
                                }

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

                        composable("home") {
                            HomeScreen(navController, mainViewModel, groupViewModel)
                        }

                        composable("search") {
                            SearchScreen(navController, mainViewModel, groupViewModel)
                        }

                        composable("favorites") {
                            FavoritesScreen(navController, mainViewModel, groupViewModel)
                        }

                        composable("group") {
                            GroupScreen(navController, mainViewModel,groupViewModel)
                        }

                        composable("settings") {
                            SettingsScreen(authViewModel, userPreferences)
                        }

                        composable(
                            route = "details/{mediaId}/{mediaType}",
                            arguments = listOf(
                                navArgument("mediaId") { type = NavType.IntType },
                                navArgument("mediaType") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
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

                            val mediaId = backStackEntry.arguments?.getInt("mediaId") ?: 0
                            val mediaTypeStr = backStackEntry.arguments?.getString("mediaType")
                            val mediaType = when (mediaTypeStr) {
                                "movie" -> MediaType.MOVIE
                                "tv" -> MediaType.TV
                                else -> null
                            }
                            if (mediaType != null) {
                                DetailsScreen(
                                    navController = navController,
                                    mediaId = mediaId,
                                    mediaType = mediaType,
                                    mainViewModel = mainViewModel,
                                    groupViewModel = groupViewModel,
                                    personViewModel = personViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
