package com.example.watch_together.tabHost

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.watch_together.models.Screen
import com.example.watch_together.viewModels.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(
    selectedScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    favoritesViewModel: FavoritesViewModel
) {

    val hasViewedFavorites by favoritesViewModel.hasViewedFavorites.collectAsState()

    NavigationBar(containerColor = Color.White.copy(0.8f), tonalElevation = 0.dp, contentColor = Color.White) {
        NavigationBarItem(
            selected = selectedScreen == Screen.Search,
            onClick = { onScreenSelected(Screen.Search) },
            icon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
            label = { Text("Поиск") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.DarkGray,
                unselectedIconColor = Color.Gray,
                selectedTextColor = Color.DarkGray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            selected = selectedScreen == Screen.Favorites,
            onClick = {
                onScreenSelected(Screen.Favorites)
                favoritesViewModel.markFavoritesAsViewed()
                favoritesViewModel.checkHasViewedFavorites()
            },
            icon = {
                BadgedBox(badge = {
                    if (!hasViewedFavorites) {
                        Badge(
                            modifier = Modifier.size(8.dp),
                            containerColor = Color.Red
                        ) { }
                    }
                }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Избранное")
                }
            },
            label = { Text("Избранное") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.DarkGray,
                unselectedIconColor = Color.Gray,
                selectedTextColor = Color.DarkGray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.White
            )
        )
        NavigationBarItem(
            selected = selectedScreen == Screen.Settings,
            onClick = { onScreenSelected(Screen.Settings) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Настройки" )},
            label = { Text("Настройки") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.DarkGray,
                unselectedIconColor = Color.Gray,
                selectedTextColor = Color.DarkGray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.White
            )
        )
    }

}
