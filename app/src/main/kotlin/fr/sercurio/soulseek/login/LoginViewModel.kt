package fr.sercurio.soulseek.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.sercurio.soulseek.SoulseekApi
import fr.sercurio.soulseek.client.server.messages.LoginMessage
import kotlinx.coroutines.launch

class LoginViewModel(private val soulseekApi: SoulseekApi) : ViewModel() {
    var loginState by mutableStateOf<LoginMessage?>(null)

    init {
        soulseekApi.onLogin {
            loginState = it
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            soulseekApi.login(username, password)
        }
    }
}