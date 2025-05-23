package com.example.watch_together.group

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.watch_together.models.Movie
import com.example.watch_together.models.Series
import com.example.watch_together.movieCards.MediaColumnItem
import com.example.watch_together.viewModels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    groupViewModel: GroupViewModel,
) {
    val groupState by groupViewModel.groupState.collectAsState()
    val mainState by mainViewModel.mainState.collectAsState()

    var showSheet by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()


    // Загружаем данные о группах, если они еще не загружены
    LaunchedEffect(Unit) {
        if (groupState.groupsWithDetails.isEmpty()) {
            groupViewModel.loadUserGroupsAndMedia()
        }
    }

    LaunchedEffect(groupState.selectedGroupId) {
        if (groupState.selectedGroupId != null) {
            groupViewModel.createInviteLinkForSelectedGroup()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showSheet = true }) {
                Icon(Icons.Default.GroupAdd, contentDescription = "Создать группу")
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Групповой экран", style = MaterialTheme.typography.titleLarge)

            if (groupState.inviteLink != null) {
                ShareInviteLinkButton(inviteLink = groupState.inviteLink)
            }

            // Если данные о группах еще не загружены, показываем индикатор загрузки
            if (groupState.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
            } else if (groupState.groupsWithDetails.isNotEmpty()) {
                // Селектор групп
                Text("Выберите группу:", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                groupState.groupsWithDetails.forEach { (group, _) ->
                    Button(
                        onClick = { groupViewModel.selectGroup(group.groupId) },
                        colors = if (groupState.selectedGroupId == group.groupId) {
                            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        } else {
                            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(group.groupName)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Содержимое выбранной группы
                val selectedGroup = groupState.groupsWithDetails.find { it.first.groupId == groupState.selectedGroupId }

                if (selectedGroup != null) {
                    val (group, moviesWithUsers) = selectedGroup
                    Text("Группа: ${group.groupName}", style = MaterialTheme.typography.titleLarge)


                    Text("Участники:", style = MaterialTheme.typography.titleSmall)
                    Column {
                        group.groupMembers.keys.forEach { userId ->
                            val username = groupState.userIdToUsername[userId] ?: userId
                            Text("• $username", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    if (moviesWithUsers.isEmpty()) {
                        Text("Фильмов пока нет", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        LazyColumn {
                            items(moviesWithUsers, key = { it.mediaItem.itemId }) { mediaWithUser ->
                                val mediaItem = mediaWithUser.mediaItem
                                val username = groupState.userIdToUsername[mediaWithUser.addedByUserId] ?: "Неизвестный"

                                Text(
                                    text = "Добавил: $username",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                                )

                                MediaColumnItem(
                                    mediaItem = mediaItem,
                                    isFavorite = mainState.favorites.any { it.itemId == mediaItem.itemId },
                                    isGroupFavorite = true,
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
                                        groupViewModel.removeMediaFromSelectedGroupFavorites(mediaItem.itemId, mediaItem.mediaType)
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Text("Выберите группу для отображения фильмов", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                Text("Вы не присоединились к группам.", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(Modifier.height(16.dp))

            Button(onClick = {
                // Удаление выбранной группы
                groupViewModel.deleteSelectedGroup()
            }, enabled = groupState.selectedGroupId != null) {
                Text("Удалить выбранную группу")
            }

            groupState.error?.let {
                Text("Ошибка: $it", color = Color.Red, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(Modifier.weight(1f))
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Создание группы", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Название группы") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (groupName.isNotBlank()) {
                            groupViewModel.createGroup(groupName)
                            showSheet = false
                            groupName = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Создать группу")
                }
            }
        }
    }
}
