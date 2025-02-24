package com.example.watch_together.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
        when (selectedTab) {
            Screen.Search -> SearchTabNavHost(movieViewModel, favoritesViewModel, paddingValues)
            Screen.Favorites -> FavoritesTabNavHost(movieViewModel, favoritesViewModel, paddingValues, savedMovieId, onMovieIdSaved = { savedMovieId = it })
            Screen.Settings -> SettingsScreen(movieViewModel, authViewModel, paddingValues)
        }

    }
}
