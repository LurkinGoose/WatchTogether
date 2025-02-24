package com.example.watch_together.tabHost

import android.util.Log
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
    Log.d("BottomNavigationBar", "🔄 Компонент BottomNavigationBar пересоздался. Текущий экран: ${selectedScreen.route}")

    val uiState by favoritesViewModel.uiState.collectAsState()
    val hasViewedFavorites = uiState.hasViewedFavorites

    Log.d("BottomNavigationBar", "💾 hasViewedFavorites = $hasViewedFavorites")

    NavigationBar(containerColor = Color.White.copy(0.8f), tonalElevation = 0.dp, contentColor = Color.White) {

        NavigationBarItem(
            selected = selectedScreen == Screen.Search,
            onClick = {
                Log.d("BottomNavigationBar", "🟢 Нажата вкладка Поиск. Текущий экран: ${selectedScreen.route}")
                if (selectedScreen != Screen.Search) {
                    Log.d("BottomNavigationBar", "✅ Переход на экран Search")
                    onScreenSelected(Screen.Search)
                } else {
                    Log.d("BottomNavigationBar", "❌ Уже на экране Search. Переход не требуется.")
                }
            },
            icon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
            label = { Text("Поиск") }
        )

        NavigationBarItem(
            selected = selectedScreen == Screen.Favorites,
            onClick = {
                Log.d("BottomNavigationBar", "🟢 Нажата вкладка Избранное. Текущий экран: ${selectedScreen.route}")
                if (selectedScreen != Screen.Favorites) {
                    Log.d("BottomNavigationBar", "✅ Переход на экран Favorites")
                    onScreenSelected(Screen.Favorites)
                    favoritesViewModel.markFavoritesAsViewed()
                } else {
                    Log.d("BottomNavigationBar", "❌ Уже на экране Favorites. Переход не требуется.")
                }
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
            label = { Text("Избранное") }
        )

        NavigationBarItem(
            selected = selectedScreen == Screen.Settings,
            onClick = {
                Log.d("BottomNavigationBar", "🟢 Нажата вкладка Настройки. Текущий экран: ${selectedScreen.route}")
                if (selectedScreen != Screen.Settings) {
                    Log.d("BottomNavigationBar", "✅ Переход на экран Settings")
                    onScreenSelected(Screen.Settings)
                } else {
                    Log.d("BottomNavigationBar", "❌ Уже на экране Settings. Переход не требуется.")
                }
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Настройки") },
            label = { Text("Настройки") }
        )
    }
}
