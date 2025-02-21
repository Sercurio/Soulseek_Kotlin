package fr.sercurio.soulseek.server.entities

data class RoomApiModel(
    val name: String,
    val private: Boolean = false,
    val owner: Boolean = false,
    val operated: Boolean = false,
    var nbUsers: Int = 0,
    var roomMessageApiModels: MutableList<RoomMessageApiModel> = mutableListOf(),
)
