package com.example.watch_together.tabHost

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.watch_together.models.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentDestination?.route
    Log.d("BottomNavigationBar", "🔄 Пересоздание BottomNavigationBar, текущий маршрут: $currentRoute")

    NavigationBar {
        listOf(
            Screen.Search to Icons.Default.Search,
            Screen.Favorites to Icons.Default.Favorite,
            Screen.Settings to Icons.Default.Settings
        ).forEach { (screen, icon) ->
            val isSelected = currentRoute == screen.route

            Log.d("BottomNavigationBar", "▶️ Экран: ${screen.route}, выбран: $isSelected")

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        Log.d("BottomNavigationBar", "✅ Переход на ${screen.route}")
                        navController.navigate(screen.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        Log.d("BottomNavigationBar", "❌ Уже на ${screen.route}, переход не нужен")
                    }
                },
                icon = { Icon(icon, contentDescription = screen.route) },
                label = { Text(screen.label) }
            )
        }
    }
}
