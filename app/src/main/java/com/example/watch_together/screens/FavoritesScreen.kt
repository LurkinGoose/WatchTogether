package com.example.watch_together.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun FavoritesScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    groupViewModel: GroupViewModel,
) {
    val mainState by mainViewModel.mainState.collectAsState()
    val groupState by groupViewModel.groupState.collectAsState()

    val selectedGroupMediaItems = remember(groupState.selectedGroupId, groupState.groupsWithDetails) {
        groupState.selectedGroupId?.let { selectedGroupId ->
            groupState.groupsWithDetails.find { it.first.groupId == selectedGroupId }?.second?.map { it.mediaItem } ?: emptyList()
        } ?: emptyList()
    }

    val snackbarHostState = remember { SnackbarHostState() }

    val favoritesColumnState = mainViewModel.favoritesColumnState

    LaunchedEffect(mainState.scrollToTop) {
        if (mainState.scrollToTop) {
            favoritesColumnState.animateScrollToItem(0)
            mainViewModel.clearScrollToTop()
        }
    }

    val context = LocalContext.current // Для Toast

    // Показываем Snackbar при изменении ошибки
    LaunchedEffect(mainState.error) {
        mainState.error?.let { errorMessage ->
            snackbarHostState.showSnackbar(errorMessage, duration = SnackbarDuration.Short)
            mainViewModel.clearError() // Очистка ошибки после показа
        }
    }

    // Показываем Snackbar при изменении ошибки группы
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
                .padding(paddingValues)  // Используем paddingValues
        ) {
            Text(
                "Избранное",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    mainState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    mainState.favorites.isEmpty() -> {
                        Text(
                            text = "Список избранных фильмов пуст",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    else -> {
                        LazyColumn(
                            state = favoritesColumnState,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(top = 8.dp, start = 8.dp, end = 8.dp)
                        ) {
                            items(mainState.favorites, key = { it.itemId }) { mediaItem ->

                                MediaColumnItem(
                                    mediaItem = mediaItem,
                                    isFavorite = true,
                                    isGroupFavorite = selectedGroupMediaItems.any { it.itemId == mediaItem.itemId },
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
                                            mainViewModel.removeFromFavorites(mediaItem.itemId, mediaItem.mediaType)
                                        } else {
                                            mainViewModel.addToFavorites(mediaItem.itemId, mediaItem.mediaType)
                                        }
                                    },
                                    onToggleGroupFavorite = {
                                        if (groupState.selectedGroupId == null) {
                                            Toast.makeText(
                                                context,
                                                "Выберите группу для добавления в избранное группы",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            val mediaType = mediaItem.mediaType
                                            if (selectedGroupMediaItems.any { it.itemId == mediaItem.itemId }) {
                                                groupViewModel.removeMediaFromSelectedGroupFavorites(
                                                    mediaItem.itemId, mediaType
                                                )
                                            } else {
                                                groupViewModel.addMediaToSelectedGroupFavorites(
                                                    mediaItem.itemId, mediaType
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}
