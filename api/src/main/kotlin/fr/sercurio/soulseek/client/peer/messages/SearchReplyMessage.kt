package fr.sercurio.soulseek.client.peer.messages

import fr.sercurio.soulseek.entities.SoulFile

data class SearchReplyMessage(
    val username: String,
    val token: Int,
    val soulFiles: ArrayList<SoulFile>,
    private val slotsFree: Boolean,
    private val avgSpeed: Int,
    private val queueLength: Long
)
