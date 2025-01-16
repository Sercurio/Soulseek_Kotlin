package fr.sercurio.soulseek.client.peer.messages

data class DownloadCompleteMessage(val username: String, val filepath: String, val file: ByteArray)
