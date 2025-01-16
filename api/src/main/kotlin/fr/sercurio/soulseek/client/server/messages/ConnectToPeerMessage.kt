package fr.sercurio.soulseek.client.server.messages

class ConnectToPeerMessage(
    val username: String,
    val type: String,
    val ip: String,
    val port: Int,
    val token: Int,
    val obfuscatedPort: Boolean,
)

enum class ConnectToPeerMessageType(val type: String) {
    PEER("P"),
    TRANSFER("F"),
}
