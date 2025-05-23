package com.example.watch_together.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watch_together.models.CombinedCreditsResponse
import com.example.watch_together.models.PersonDetailsResponse
import com.example.watch_together.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PersonState(
    val details: PersonDetailsResponse? = null,
    val credits: CombinedCreditsResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PersonViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _personState = MutableStateFlow(PersonState())
    val personState: StateFlow<PersonState> = _personState

    fun loadPersonData(personId: Int) {
        viewModelScope.launch {
            _personState.update { it.copy(isLoading = true, error = null) }
            try {
                val details = repository.getPersonDetails(personId)
                val credits = repository.getPersonCredits(personId)
                _personState.update {
                    it.copy(
                        details = details,
                        credits = credits,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _personState.update { it.copy(error = e.localizedMessage, isLoading = false) }
            }
        }
    }

    fun clearError() {
        _personState.update { it.copy(error = null) }
    }
}
