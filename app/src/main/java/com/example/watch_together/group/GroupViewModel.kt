package com.example.watch_together.group

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watch_together.auth.UserPreferences
import com.example.watch_together.models.MediaItem
import com.example.watch_together.models.MediaType
import com.example.watch_together.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupsUiState(
    val groupsWithDetails: List<Pair<Group, List<MovieWithUser>>> = emptyList(),
    val selectedGroupId: String? = null,
    val userIdToUsername: Map<String, String> = emptyMap(),
    val inviteLink: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class MovieWithUser(
    val mediaItem: MediaItem,
    val addedByUserId: String
)

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _groupState = MutableStateFlow(GroupsUiState())
    val groupState: StateFlow<GroupsUiState> = _groupState

    init {
        val savedGroupId = userPreferences.getSelectedGroupId()
        if (savedGroupId != null) {
            _groupState.update { it.copy(selectedGroupId = savedGroupId) }
        }
        loadUserGroupsAndMedia()
    }

    private suspend fun fetchUsernamesForUserIds(userIds: Set<String>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (uid in userIds) {
            val username = try {
                repository.getUsernameByUserId(uid) ?: uid
            } catch (e: Exception) {
                uid
            }
            map[uid] = username
        }
        return map
    }

    fun loadUserGroupsAndMedia() {
        viewModelScope.launch {
            _groupState.update { it.copy(isLoading = true, error = null) }
            try {
                val groups = repository.getUserGroupsWithMedia()

                // Собираем все userId из групп и медиа
                val allUserIds = mutableSetOf<String>()
                groups.forEach { group ->
                    allUserIds.addAll(group.groupMembers.keys)
                    allUserIds.add(group.createdBy)
                    group.groupMedia.values.forEach { mediaAddedBy ->
                        allUserIds.add(mediaAddedBy.addedBy)
                    }
                }

                val userIdToUsername = fetchUsernamesForUserIds(allUserIds)

                // Формируем список MovieWithUser с mediaItem (MediaItem)
                val groupsWithDetails = groups.map { group ->
                    val mediaWithUsers = group.groupMedia.mapNotNull { (key, mediaAddedBy) ->
                        // key = "movie_123" или "tv_456"
                        val parts = key.split("_")
                        if (parts.size != 2) return@mapNotNull null
                        val mediaTypeStr = parts[0]
                        val mediaId = parts[1].toIntOrNull() ?: return@mapNotNull null
                        val mediaType = try {
                            MediaType.valueOf(mediaTypeStr.uppercase())
                        } catch (e: Exception) {
                            return@mapNotNull null
                        }
                        val mediaItem = try {
                            repository.getMediaById(mediaId, mediaType)
                        } catch (e: Exception) {
                            null
                        }
                        if (mediaItem != null) {
                            MovieWithUser(mediaItem, mediaAddedBy.addedBy)
                        } else null
                    }
                    group to mediaWithUsers
                }

                val savedGroupId = userPreferences.getSelectedGroupId()
                val validGroupId = if (savedGroupId != null && groups.any { it.groupId == savedGroupId }) {
                    savedGroupId
                } else {
                    groupsWithDetails.firstOrNull()?.first?.groupId
                }

                _groupState.update {
                    it.copy(
                        groupsWithDetails = groupsWithDetails,
                        isLoading = false,
                        error = null,
                        userIdToUsername = userIdToUsername,
                        selectedGroupId = validGroupId
                    )
                }
            } catch (e: Exception) {
                _groupState.update { it.copy(error = "Ошибка загрузки групп: ${e.message}", isLoading = false) }
            }
        }
    }

    fun addMediaToSelectedGroupFavorites(mediaId: Int, mediaType: MediaType) {
        val groupId = _groupState.value.selectedGroupId ?: return
        viewModelScope.launch {
            try {
                repository.addMediaToGroupFavorites(groupId, mediaId.toString(), mediaType)
                val mediaItem = repository.getMediaById(mediaId, mediaType)
                val currentUserId = repository.getCurrentUserId()
                _groupState.update { state ->
                    val updatedGroupsWithDetails = state.groupsWithDetails.map { (group, mediaWithUsers) ->
                        if (group.groupId == groupId) {
                            val newMediaWithUser = MovieWithUser(mediaItem, addedByUserId = currentUserId)
                            group to (listOf(newMediaWithUser) + mediaWithUsers)
                        } else {
                            group to mediaWithUsers
                        }
                    }
                    state.copy(groupsWithDetails = updatedGroupsWithDetails)
                }
            } catch (e: Exception) {
                _groupState.update { it.copy(error = "Ошибка добавления медиа: ${e.message}") }
            }
        }
    }

    fun removeMediaFromSelectedGroupFavorites(mediaId: Int, mediaType: MediaType) {
        val groupId = _groupState.value.selectedGroupId ?: return
        viewModelScope.launch {
            try {
                repository.removeMediaFromGroupFavorites(groupId, mediaId.toString(), mediaType)
                _groupState.update { state ->
                    val updatedGroupsWithDetails = state.groupsWithDetails.map { (group, mediaWithUsers) ->
                        if (group.groupId == groupId) {
                            val filteredMedia = mediaWithUsers.filter { it.mediaItem.itemId != mediaId }
                            group to filteredMedia
                        } else {
                            group to mediaWithUsers
                        }
                    }
                    state.copy(groupsWithDetails = updatedGroupsWithDetails)
                }
            } catch (e: Exception) {
                _groupState.update { it.copy(error = "Ошибка удаления медиа: ${e.message}") }
            }
        }
    }

    fun selectGroup(groupId: String) {
        _groupState.update { it.copy(selectedGroupId = groupId) }
        userPreferences.saveSelectedGroupId(groupId)
    }

    fun createGroup(groupName: String) {
        viewModelScope.launch {
            _groupState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.createGroup(groupName)
                loadUserGroupsAndMedia()
            } catch (e: Exception) {
                _groupState.update { it.copy(error = "Ошибка создания группы: ${e.message}", isLoading = false) }
            }
        }
    }

    fun deleteSelectedGroup() {
        val groupId = _groupState.value.selectedGroupId ?: return
        viewModelScope.launch {
            _groupState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.deleteGroup(groupId)
                loadUserGroupsAndMedia()
            } catch (e: Exception) {
                _groupState.update { it.copy(error = "Ошибка удаления группы: ${e.message}", isLoading = false) }
            }
        }
    }

    fun createInviteLinkForSelectedGroup() {
        val groupId = _groupState.value.selectedGroupId ?: return

        viewModelScope.launch {
            try {
                val inviteLink = repository.createInviteLink(groupId)
                _groupState.update { it.copy(inviteLink = inviteLink, error = null) }
            } catch (e: Exception) {
                _groupState.update { it.copy(error = "Ошибка создания ссылки приглашения: ${e.message}") }
            }
        }
    }

    fun joinGroupFromInvite(groupId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = repository.getCurrentUserId()
                repository.inviteMemberToGroup(groupId, currentUserId)

                // Обновляем список групп
                loadUserGroupsAndMedia()

                // Можно показать SnackBar или навигацию
                _groupState.update { it.copy(selectedGroupId = groupId) }

                Log.d("GroupViewModel", "✅ Пользователь добавлен в группу по ссылке: $groupId")
            } catch (e: Exception) {
                Log.e("GroupViewModel", "❌ Ошибка при добавлении в группу: ${e.message}")
                _groupState.update { it.copy(error = "Ошибка вступления в группу по ссылке") }
            }
        }
    }


    fun clearError() {
        _groupState.update { it.copy(error = null) }
    }
}
