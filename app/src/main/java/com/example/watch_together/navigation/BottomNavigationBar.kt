package com.example.watch_together.navigation

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.watch_together.viewModels.MoviesViewModel

@Composable
fun BottomNavigationBar(
    modifier: Modifier,
    navController: NavHostController,
    currentRoute: String,
    moviesViewModel: MoviesViewModel
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") },
            selected = currentRoute == "search",
            onClick = {
                if (currentRoute != "search") {
                    navController.navigate("search") {
                        launchSingleTop = true
                        popUpTo("search") { inclusive = true }
                    }
                } else {
                    moviesViewModel.resetSearchScrollPosition()
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
            label = { Text("Favorites") },
            selected = currentRoute == "favorites",
            onClick = {
                if (currentRoute != "favorites") {
                    navController.navigate("favorites") {
                        launchSingleTop = true
                        popUpTo("favorites") { inclusive = true }
                    }
                } else {
                    moviesViewModel.resetFavoritesScrollPosition()
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentRoute == "settings",
            onClick = {
                if (currentRoute != "settings") {
                    // Переход на экран настроек
                    navController.navigate("settings") {
                        launchSingleTop = true
                        popUpTo("settings") { inclusive = true }
                    }
                }
            }
        )
    }
}
