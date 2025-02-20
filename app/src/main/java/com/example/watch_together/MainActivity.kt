package com.example.watch_together

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.watch_together.ui.theme.Watch_TogetherTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn


class MainActivity : ComponentActivity() {
    private val movieViewModel: MovieViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            task.addOnSuccessListener { account ->
                Log.d("MainActivity", "Google Sign-In Success: ${account.email}")
                authViewModel.signInWithGoogle(account) { success, error ->
                    if (success) {
                        Log.d("MainActivity", "Firebase Auth Success")
                    } else {
                        Log.e("MainActivity", "Firebase Auth Failed: $error")
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("MainActivity", "Google Sign-In Failed", e)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Watch_TogetherTheme {
                var isAuthenticated by remember { mutableStateOf(false) }

                LaunchedEffect(authViewModel.user.collectAsState().value) {
                    isAuthenticated = authViewModel.user.value != null
                }

                if (!isAuthenticated) {
                    AuthScreen(authViewModel, googleSignInLauncher) {
                        isAuthenticated = true
                    }
                } else {
                    MainScreen(movieViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MovieViewModel) {
    var selectedScreen by rememberSaveable { mutableStateOf(Screen.Search) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedScreen) { selectedScreen = it }
        }
    ) { paddingValues ->
        when (selectedScreen) {
            Screen.Search -> SearchScreen(viewModel, paddingValues = paddingValues)
            Screen.Favorites -> FavoritesScreen(Modifier.padding(paddingValues))
            Screen.Settings -> SettingsScreen(Modifier.padding(paddingValues))
        }
    }
}

@Composable
fun BottomNavigationBar(selectedScreen: Screen, onScreenSelected: (Screen) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedScreen == Screen.Search,
            onClick = { onScreenSelected(Screen.Search) },
            icon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
            label = { Text("Поиск") }
        )
        NavigationBarItem(
            selected = selectedScreen == Screen.Favorites,
            onClick = { onScreenSelected(Screen.Favorites) },
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Избранное") },
            label = { Text("Избранное") }
        )
        NavigationBarItem(
            selected = selectedScreen == Screen.Settings,
            onClick = { onScreenSelected(Screen.Settings) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Настройки") },
            label = { Text("Настройки") }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val viewModel = MovieViewModel()
    Watch_TogetherTheme {
        MainScreen(viewModel)
    }
}
