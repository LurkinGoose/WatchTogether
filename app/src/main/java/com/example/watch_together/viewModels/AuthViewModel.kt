package com.example.watch_together.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.*
import kotlinx.coroutines.flow.*

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val _authState = MutableStateFlow(
        if (auth.currentUser != null) AuthState.Authenticated else AuthState.Unauthenticated
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("533333692169-q6e6phaa3k9rpg3krd42hudbg6vc8mfa.apps.googleusercontent.com")
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, options)
    }

    fun signInWithGoogle(account: GoogleSignInAccount, onResult: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _user.value = auth.currentUser
                _authState.value = if (task.isSuccessful) AuthState.Authenticated else AuthState.Unauthenticated
                onResult(task.isSuccessful, task.exception?.message)
            }
    }

    fun signOut(context: Context, movieViewModel: MovieViewModel) {
        val googleSignInClient = getGoogleSignInClient(context)
        googleSignInClient.signOut().addOnCompleteListener {
            googleSignInClient.revokeAccess().addOnCompleteListener {
                auth.signOut()
                _user.value = null
                _authState.value = AuthState.Unauthenticated

                movieViewModel.clearMovies()
            }
        }
    }
}
