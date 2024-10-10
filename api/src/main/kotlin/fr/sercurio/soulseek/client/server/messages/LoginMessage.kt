package fr.sercurio.soulseek.client.server.messages

data class LoginMessage(
    val connected: Boolean,
    val greeting: String?,
    val ip: Int?,
    val reason: String?
)
