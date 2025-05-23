package com.example.watch_together.navigation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.watch_together.viewModels.MainViewModel

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: String,
    mainViewModel: MainViewModel,
    onResetScroll: () -> Unit,
) {

    val mainState = mainViewModel.mainState.collectAsState()

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = {
                if (currentRoute != "home") {
                    navController.navigate("home") {
                        launchSingleTop = true
                        popUpTo("home") { inclusive = true }
                    }
                } else {
                    onResetScroll()
                }
            }
        )

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
                    onResetScroll()
                }
            }
        )
        NavigationBarItem(
            icon = {
                Box {
                    Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    if (mainState.value.hasNewFavorites) {
                        Box(
                            Modifier
                                .size(8.dp)
                                .background(Color.Red, shape = CircleShape)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            },
            label = { Text("Favorites") },
            selected = currentRoute == "favorites",
            onClick = {
                if (currentRoute != "favorites") {
                    navController.navigate("favorites") {
                        launchSingleTop = true
                        popUpTo("favorites") { inclusive = true }
                    }
                    mainViewModel.clearNewFavoritesMarker()
                } else {
                    onResetScroll()
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Groups, contentDescription = "Group") },
            label = { Text("Group") },
            selected = currentRoute == "group",
            onClick = {
                if (currentRoute != "group") {
                    navController.navigate("group") {
                        launchSingleTop = true
                        popUpTo("group") { inclusive = true }
                    }
                } else {
                    onResetScroll()
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
