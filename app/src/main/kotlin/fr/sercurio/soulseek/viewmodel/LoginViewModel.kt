package fr.sercurio.soulseek.viewmodel

import androidx.lifecycle.ViewModel
import fr.sercurio.soulseek.entities.LoginApiModel
import fr.sercurio.soulseek.repositories.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {
    // Expose screen UI state
    private val _uiState = MutableStateFlow(LoginRepository.getLoginStatus())
    val uiState: StateFlow<LoginApiModel> = _uiState.asStateFlow()

    fun updateConnected(isConnected: Boolean) {
        _uiState.update { currentUiState ->
            currentUiState.copy(connected = isConnected)
        }
    }
}