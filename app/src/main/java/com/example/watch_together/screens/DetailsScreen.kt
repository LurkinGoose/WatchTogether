package com.example.watch_together.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.watch_together.group.GroupViewModel
import com.example.watch_together.models.MediaType
import com.example.watch_together.models.Movie
import com.example.watch_together.models.Series
import com.example.watch_together.movieCards.MediaDetailsCard
import com.example.watch_together.viewModels.MainViewModel
import com.example.watch_together.viewModels.PersonViewModel

@Composable
fun DetailsScreen(
    navController: NavController,
    mediaId: Int,
    mediaType: MediaType,
    mainViewModel: MainViewModel,
    groupViewModel: GroupViewModel,
    personViewModel: PersonViewModel

) {

    val mainState by mainViewModel.mainState.collectAsState()
    val groupState by groupViewModel.groupState.collectAsState()

    val context = LocalContext.current // Для Toast

    val selectedGroupMediaItems = remember(groupState.selectedGroupId, groupState.groupsWithDetails) {
        groupState.selectedGroupId?.let { selectedGroupId ->
            groupState.groupsWithDetails.find { it.first.groupId == selectedGroupId }?.second?.map { it.mediaItem } ?: emptyList()
        } ?: emptyList()
    }

    LaunchedEffect(mediaId) {
        mainViewModel.getMediaDetails(mediaId, mediaType)
    }

    val mediaItem = when (mediaType) {
        MediaType.MOVIE -> mainState.movies.details
        MediaType.TV -> mainState.series.details
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            mainState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            mainState.error != null -> Text(
                text = "Ошибка: ${mainState.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
            mediaItem != null -> {
                MediaDetailsCard(
                    mediaItem = mediaItem,
                    isFavorite = mainState.favorites.any { it.itemId == mediaItem.itemId },
                    isGroupFavorite = selectedGroupMediaItems.any { it.itemId == mediaItem.itemId },
                    onDismiss = { navController.popBackStack() },
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
