package com.example.watch_together.viewModels

import android.app.Application
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.watch_together.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import androidx.credentials.CustomCredential
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


data class AuthState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    init {
        _state.value = _state.value.copy(user = auth.currentUser)
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                _state.value = _state.value.copy(user = result.user, isLoading = false, error = null)
            } catch (e: Exception) {
                val message = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Неверный email или пароль"
                    is FirebaseAuthInvalidUserException -> "Аккаунт с таким email не найден"
                    is FirebaseNetworkException -> "Нет подключения к интернету"
                    else -> "Ошибка входа: ${e.message}"
                }
                _state.value = _state.value.copy(isLoading = false, error = message)
            }
        }
    }

    fun signUpWithEmail(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build()
                    user.updateProfile(profileUpdate).await()

                    _state.value = _state.value.copy(user = user, isLoading = false, error = null)
                } else {
                    _state.value = _state.value.copy(isLoading = false, error = "Ошибка: не удалось создать пользователя")
                }
            } catch (e: Exception) {
                val message = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Неверный формат email"
                    is FirebaseAuthUserCollisionException -> "Этот email уже зарегистрирован"
                    is FirebaseNetworkException -> "Нет подключения к интернету"
                    else -> "Ошибка регистрации: ${e.message}"
                }
                _state.value = _state.value.copy(isLoading = false, error = message)
            }
        }
    }

    fun signInWithGoogle(context: Context) {
        val credentialManager = CredentialManager.create(context)

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .build()

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val result = withContext(Dispatchers.IO) {
                    credentialManager.getCredential(request = request, context = context)
                }
                handleGoogleSignIn(result)
            } catch (e: GetCredentialException) {
                val message = when {
                    e.message?.contains("User cancelled the selector", ignoreCase = true) == true -> null
                    else -> "Ошибка Google-входа: ${e.message}"
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = message
                )
            }
        }
    }


    private suspend fun handleGoogleSignIn(result: GetCredentialResponse) {
        val googleCredential = when (val credential = result.credential) {
            is GoogleIdTokenCredential -> credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    GoogleIdTokenCredential.createFrom(credential.data)
                } else null
            }
            else -> null
        }

        val idToken = googleCredential?.idToken
        val email = googleCredential?.id

        if (!idToken.isNullOrEmpty()) {
            try {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                val user = authResult.user

                user?.let {
                    if (user.displayName.isNullOrBlank()) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(email)
                            .build()
                        user.updateProfile(profileUpdates).await()
                    }

                    _state.value = _state.value.copy(user = user, isLoading = false, error = null)
                } ?: run {
                    _state.value = _state.value.copy(isLoading = false, error = "Ошибка входа: пользователь не найден")
                }
            } catch (e: Exception) {
                val message = when (e) {
                    is FirebaseAuthUserCollisionException -> "Этот Google-аккаунт уже зарегистрирован другим способом"
                    is FirebaseNetworkException -> "Нет подключения к интернету"
                    else -> "Ошибка Google-входа: ${e.message}"
                }
                _state.value = _state.value.copy(isLoading = false, error = message)
            }
        } else {
            _state.value = _state.value.copy(isLoading = false, error = "ID Token пуст")
        }
    }


    fun signOut() {
        auth.signOut()
        _state.value = _state.value.copy(user = null)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}