package fr.sercurio.soulseek.client.peer.messages

import fr.sercurio.soulseek.client.shared.model.SoulFile

data class SearchReplyMessage(
    val username: String,
    val token: Int,
    val soulFiles: ArrayList<SoulFile>,
    val slotsFree: Boolean,
    val avgSpeed: Int,
    val queueLength: Long,
)
