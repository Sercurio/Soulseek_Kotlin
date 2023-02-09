package fr.sercurio.soulseekapi.repositories

import fr.sercurio.soulseekapi.entities.LoginApiModel
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