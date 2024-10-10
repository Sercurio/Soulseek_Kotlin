package fr.sercurio.soulseek.repositories

import fr.sercurio.soulseek.entities.LoginApiModel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object LoginRepository {
    private val loginStatusMutex = Mutex()
    private var loginStatus: LoginApiModel = LoginApiModel(false, "")

    suspend fun setLoginStatus(loginStatus: LoginApiModel) {
        loginStatusMutex.withLock {
            LoginRepository.loginStatus = loginStatus
        }
    }

    fun getLoginStatus(): LoginApiModel {
        return loginStatus
    }
}