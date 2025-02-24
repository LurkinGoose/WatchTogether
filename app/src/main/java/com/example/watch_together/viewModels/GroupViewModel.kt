package com.example.watch_together.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.util.UUID

class GroupViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().getReference("groups")
    private val auth = FirebaseAuth.getInstance()

    // Создание группы
    fun createGroup(groupName: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val groupId = UUID.randomUUID().toString()

            val groupData = mapOf(
                "groupName" to groupName,
                "createdBy" to userId,
                "groupMembers/$userId/joinedAt" to System.currentTimeMillis().toString()
            )

            database.child(groupId).updateChildren(groupData)
                .addOnSuccessListener { onSuccess(groupId) }
                .addOnFailureListener { onFailure(it) }
        }
    }

    // Генерация ссылки-приглашения
    fun generateInviteLink(groupId: String): String {
        return "https://watch_together/join?groupId=$groupId"
    }

    // Присоединение к группе
    fun joinGroup(groupId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            val memberData = mapOf(
                "groupMembers/$userId/joinedAt" to System.currentTimeMillis().toString()
            )

            database.child(groupId).updateChildren(memberData)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        }
    }
}