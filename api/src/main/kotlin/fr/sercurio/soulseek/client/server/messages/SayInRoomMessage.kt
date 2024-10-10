package fr.sercurio.soulseek.client.server.messages

data class SayInRoomMessage(val room: String, val username: String, val message: String)