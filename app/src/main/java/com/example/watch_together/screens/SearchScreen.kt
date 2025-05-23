package com.example.watch_together.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.watch_together.group.GroupViewModel
import com.example.watch_together.models.Movie
import com.example.watch_together.models.Series
import com.example.watch_together.movieCards.MediaColumnItem
import com.example.watch_together.viewModels.MainViewModel

@Composable
fun SearchScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    groupViewModel: GroupViewModel,
) {
    var query by rememberSaveable { mutableStateOf("") }

    val mainState by mainViewModel.mainState.collectAsState()
    val groupState by groupViewModel.groupState.collectAsState()

    val searchColumnState = mainViewModel.searchColumnState

    val selectedGroupMediaItem = remember(groupState.selectedGroupId, groupState.groupsWithDetails) {
        groupState.selectedGroupId?.let { selectedGroupId ->
            groupState.groupsWithDetails.find { it.first.groupId == selectedGroupId }?.second?.map { it.mediaItem } ?: emptyList()
        } ?: emptyList()
    }

    LaunchedEffect(mainState.scrollToTop) {
        if (mainState.scrollToTop) {
            searchColumnState.animateScrollToItem(0)
            mainViewModel.clearScrollToTop()
        }
    }

    val context = LocalContext.current // Для Toast
    val snackbarHostState = remember { SnackbarHostState() }

    // Показываем Snackbar при изменении ошибки в mainState
    LaunchedEffect(mainState.error) {
        mainState.error?.let { errorMessage ->
            snackbarHostState.showSnackbar(errorMessage, duration = SnackbarDuration.Short)
            mainViewModel.clearError() // Очистка ошибки после показа
        }
    }

    // Показываем Snackbar при изменении ошибки в groupState
    LaunchedEffect(groupState.error) {
        groupState.error?.let { errorMessage ->
            snackbarHostState.showSnackbar(errorMessage, duration = SnackbarDuration.Short)
            groupViewModel.clearError() // Очистка ошибки после показа
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(paddingValues)
        ) {

            TextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Введите название фильма") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { mainViewModel.searchMedia(query) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Поиск")
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    mainState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    mainState.searchResults.isEmpty() -> {
                        Text(
                            text = "Нет результатов для поиска",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    else -> {
                        LazyColumn(state = searchColumnState) {
                            items(mainState.searchResults, key = { it.itemId }) { mediaItem ->

                                MediaColumnItem(
                                    mediaItem = mediaItem,
                                    isFavorite = mainState.favorites.any { it.itemId == mediaItem.itemId }.also {
                                        Log.d("FAVORITES_DEBUG", "MediaItem: ${mediaItem.titleText}, isFavorite: $it")
                                    },
                                    isGroupFavorite = selectedGroupMediaItem.any { it.itemId == mediaItem.itemId },
                                    onClick = {
                                        when (mediaItem) {
                                            is Movie -> {
                                                navController.navigate("details/${mediaItem.id}/movie")
                                            }
                                            is Series -> {
                                                navController.navigate("details/${mediaItem.id}/tv")
                                            }
                                        }
                                    },
                                    onToggleFavorite = {
                                        if (mainState.favorites.any { it.itemId == mediaItem.itemId }) {
                                            Log.d("FAVORITES_DEBUG", "Removing from favorites: ${mediaItem.titleText}")
                                            mainViewModel.removeFromFavorites(mediaItem.itemId, mediaItem.mediaType)
                                        } else {
                                            Log.d("FAVORITES_DEBUG", "Adding to favorites: ${mediaItem.titleText}")
                                            mainViewModel.addToFavorites(mediaItem.itemId, mediaItem.mediaType)
                                        }
                                    },
                                    onToggleGroupFavorite = {
                                        if (groupState.selectedGroupId == null) {
                                            Toast.makeText(context, "Выберите группу для добавления в избранное группы", Toast.LENGTH_SHORT).show()
                                        } else {
                                            val mediaType = mediaItem.mediaType // предполагаем, что MediaItem содержит поле mediaType типа MediaType
                                            if (selectedGroupMediaItem.any { it.itemId == mediaItem.itemId }) {
                                                // Удаляем из группы
                                                groupViewModel.removeMediaFromSelectedGroupFavorites(mediaItem.itemId, mediaType)
                                            } else {
                                                // Добавляем в группу
                                                groupViewModel.addMediaToSelectedGroupFavorites(mediaItem.itemId, mediaType)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // Показываем Snackbar, если есть ошибка
                mainState.error?.let { error ->
                    Snackbar(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        action = {
                            TextButton(onClick = { mainViewModel.clearError() }) {
                                Text("Ок")
                            }
                        }
                    ) {
                        Text(error)
                    }
                }
            }
        }
    }
}
