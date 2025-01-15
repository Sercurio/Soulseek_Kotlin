package fr.sercurio.soulseek.exposition.login.model

data class LoginResponse(
    val connected: Boolean,
    val greeting: String?,
    val ip: Int?,
    val reason: String?
)
