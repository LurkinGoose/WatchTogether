package com.example.watch_together.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.watch_together.models.Screen
import com.example.watch_together.tabHost.BottomNavigationBar
import com.example.watch_together.tabHost.FavoritesTabNavHost
import com.example.watch_together.tabHost.SearchTabNavHost
import com.example.watch_together.viewModels.AuthViewModel
import com.example.watch_together.viewModels.FavoritesViewModel
import com.example.watch_together.viewModels.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    movieViewModel: MovieViewModel,
    authViewModel: AuthViewModel,
    favoritesViewModel: FavoritesViewModel
) {
    var selectedTab by rememberSaveable { mutableStateOf(Screen.Search) }
    var savedMovieId by rememberSaveable { mutableStateOf<Int?>(null) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = selectedTab,
                onScreenSelected = { screen -> selectedTab = screen },
                favoritesViewModel = favoritesViewModel
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AnimatedContent(
                targetState = selectedTab,
                label = "MainScreen_AnimatedContent"
            ) { currentScreen ->
                when (currentScreen) {
                    Screen.Search -> key("search") {
                        SearchTabNavHost(
                            movieViewModel = movieViewModel,
                            favoritesViewModel = favoritesViewModel,
                            paddingValues = paddingValues
                        )
                    }
                    Screen.Favorites -> key("favorites") {
                        FavoritesTabNavHost(
                            movieViewModel = movieViewModel,
                            favoritesViewModel = favoritesViewModel,
                            paddingValues = paddingValues,
                            savedMovieId = savedMovieId,
                            onMovieIdSaved = { savedMovieId = it }
                        )
                    }
                    Screen.Settings -> key("settings") {
                        SettingsScreen(
                            movieViewModel = movieViewModel,
                            authViewModel = authViewModel,
                            paddingValues = paddingValues
                        )
                    }
                }
            }
        }
    }
}
