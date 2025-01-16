package fr.sercurio.soulseek.client.server.messages

import fr.sercurio.soulseek.server.entities.RoomApiModel

data class RoomListMessage(val rooms: List<RoomApiModel>)
