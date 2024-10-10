package fr.sercurio.soulseek.client.peer.messages

data class TransferRequestMessage(
    val direction: Int,
    val token: Int,
    val path: String,
    val size: Long
)
