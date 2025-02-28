package com.example.watch_together.navigation

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

@Composable
fun BottomNavigationBar(modifier: Modifier, navController: NavHostController
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Home") },
            label = { Text("Search") },
            selected = false,
            onClick = { navController.navigate("search") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
            label = { Text("Favorites") },
            selected = false,
            onClick = { navController.navigate("favorites") }
        )
        NavigationBarItem(icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = false,
            onClick = { navController.navigate("settings") }
        )
    }
}