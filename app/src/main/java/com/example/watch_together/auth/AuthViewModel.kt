package com.example.watch_together.auth

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.example.watch_together.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    application: Application
) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(application.applicationContext)

    private val _uiState = MutableStateFlow(AuthUiState(user = userPreferences.getUser()))
    val uiState: StateFlow<AuthUiState> = _uiState

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.SignInWithEmail -> signInWithEmail(intent.email, intent.password)
            is AuthIntent.SignUpWithEmail -> signUpWithEmail(intent.fullName, intent.email, intent.password)
            is AuthIntent.SignInWithGoogle -> signInWithGoogle(intent.context)
            AuthIntent.SignOut -> signOut()
            AuthIntent.ClearError -> clearError()
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val providers = getProvidersForEmail(email)

                if (providers.isNotEmpty() && !providers.contains("password")) {
                    // Аккаунт есть, но вход по паролю невозможен, тк создан через Google
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Этот email зарегистрирован через Google. Войдите через Google."
                    )
                    return@launch
                }

                val result = auth.signInWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    userPreferences.saveUser(user)
                    _uiState.value = _uiState.value.copy(user = user, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = when (e) {
                        is FirebaseAuthInvalidCredentialsException -> "Неверный email или пароль"
                        is FirebaseAuthInvalidUserException -> "Аккаунт с таким email не найден"
                        is FirebaseNetworkException -> "Нет подключения к интернету"
                        else -> "Ошибка входа: ${e.message}"
                    }
                )
            }
        }
    }


    private fun isValidEmail(email: String): Boolean {
        return email.trim().isNotEmpty() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }


    private suspend fun getProvidersForEmail(email: String): List<String> =
        auth.fetchSignInMethodsForEmail(email).await().signInMethods ?: emptyList()


    private fun signUpWithEmail(fullName: String, email: String, password: String) {
        Log.d("AuthViewModel", "Email: '$email'")

        if (!isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(error = "Введите корректный email")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val providers = getProvidersForEmail(email)

                if (providers.isNotEmpty()) {
                    // Если аккаунт уже зарегистрирован с помощью Google или другого провайдера, но не email/password
                    if (!providers.contains("password")) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Этот email уже зарегистрирован через Google. Войдите через Google."
                        )
                        return@launch
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Этот email уже зарегистрирован."
                        )
                        return@launch
                    }
                }

                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user ?: throw Exception("Пользователь не создан")

                // Обновляем имя пользователя
                user.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build()
                ).await()

                user.reload().await()

                FirebaseDatabase.getInstance().getReference("users")
                    .child(user.uid)
                    .setValue(mapOf("username" to fullName)).await()

                userPreferences.saveUser(user)
                _uiState.value = _uiState.value.copy(user = user, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = when (e) {
                        is FirebaseAuthInvalidCredentialsException -> "Неверный формат email"
                        is FirebaseAuthUserCollisionException -> "Этот email уже зарегистрирован"
                        is FirebaseNetworkException -> "Нет подключения к интернету"
                        else -> "Ошибка регистрации: ${e.message}"
                    }
                )
            }
        }
    }


    private fun signInWithGoogle(context: Context) {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            ).build()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val result = withContext(Dispatchers.IO) {
                    credentialManager.getCredential(request = request, context = context)
                }
                handleGoogleCredential(result)
            } catch (e: GetCredentialException) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    private suspend fun handleGoogleCredential(response: GetCredentialResponse) {
        val googleCredential = when (val cred = response.credential) {
            is GoogleIdTokenCredential -> cred
            is CustomCredential -> {
                if (cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    GoogleIdTokenCredential.createFrom(cred.data)
                } else null
            }
            else -> null
        }

        val idToken = googleCredential?.idToken ?: return run {
            _uiState.value = _uiState.value.copy(isLoading = false, error = "ID Token пуст")
        }

        try {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(firebaseCredential).await()
            result.user?.let { user ->
                userPreferences.saveUser(user) // Сохраняем пользователя в UserPreferences
                _uiState.value = _uiState.value.copy(user = user, isLoading = false)
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = when (e) {
                    is FirebaseAuthUserCollisionException -> "Этот Google-аккаунт уже зарегистрирован другим способом"
                    is FirebaseNetworkException -> "Нет подключения к интернету"
                    else -> "Ошибка Google-входа: ${e.message}"
                }
            )
        }
    }

    private fun signOut() {
        auth.signOut()
        userPreferences.clearUser() // Очищаем сохраненного пользователя
        _uiState.value = _uiState.value.copy(user = null)
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
