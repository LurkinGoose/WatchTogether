package com.example.watch_together.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jakarta.inject.Inject

class UserPreferences @Inject constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(user: FirebaseUser) {
        sharedPreferences.edit {
            putString("user_uid", user.uid)
            putString("user_name", user.displayName ?: "")
        }
    }

    fun getUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    private fun getUserId(): String? {
        return sharedPreferences.getString("user_uid", null)
    }

    fun getUserName(): String? {
        return sharedPreferences.getString("user_name", null)
    }

    fun requireUserId(): String {
        return getUserId() ?: throw IllegalStateException("User ID is null. Пользователь не авторизован.")
    }

    fun requireUserName(): String {
        return getUserName() ?: throw IllegalStateException("User Name is null. Пользователь не авторизован.")
    }


    fun clearUser() {
        sharedPreferences.edit {
            remove("user_uid")
            remove("user_name")
        }
    }

    companion object {
        private const val HAS_NEW_FAVORITES_KEY = "has_new_favorites"
        private const val KEY_SELECTED_GROUP_ID = "selected_group_id"
    }

    fun setHasNewFavorites(value: Boolean) {
        sharedPreferences.edit { putBoolean(HAS_NEW_FAVORITES_KEY, value) }
    }

    fun getHasNewFavorites(): Boolean {
        return sharedPreferences.getBoolean(HAS_NEW_FAVORITES_KEY, false)
    }

    fun saveSelectedGroupId(groupId: String) {
        sharedPreferences.edit { putString(KEY_SELECTED_GROUP_ID, groupId) }
    }

    fun getSelectedGroupId(): String? {
        return sharedPreferences.getString(KEY_SELECTED_GROUP_ID, null)
    }
}
