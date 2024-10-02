package fr.sercurio.soulseek.client

typealias onLoginCallback = ((LoginResponse) -> Unit)

class LoginService(private val serverSocket: ServerSocket) {
  suspend fun login(login: String, pwd: String, onLogin: onLoginCallback) {
    while (!serverSocket.connected.get()) {}

    serverSocket.login(login, pwd, onLogin)
  }
}

data class LoginResponse(
    val isConnected: Boolean,
    val greeting: String? = null,
    val ip: Int? = null,
    val reason: String? = null
)
