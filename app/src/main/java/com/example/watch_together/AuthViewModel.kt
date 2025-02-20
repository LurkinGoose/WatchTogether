package com.example.watch_together

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application): AndroidViewModel(application){
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("533333692169-q6e6phaa3k9rpg3krd42hudbg6vc8mfa.apps.googleusercontent.com")
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, options)
    }

    fun signInWithGoogle(account: GoogleSignInAccount, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            Log.d("AuthViewModel", "Attempting Firebase sign-in with Google")

            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("AuthViewModel", "Firebase Auth Success: ${auth.currentUser?.email}")
                        auth.currentUser?.reload()?.addOnCompleteListener {
                            _user.value = auth.currentUser
                            Log.d("AuthViewModel", "User state updated: ${_user.value?.email}")
                            onResult(true, null)
                        }
                    } else {
                        Log.e("AuthViewModel", "Firebase Auth Failed", task.exception)
                        onResult(false, task.exception?.message)
                    }
                }
        }
    }


    fun signOut() {
        auth.signOut()
        _user.value = null
    }
}