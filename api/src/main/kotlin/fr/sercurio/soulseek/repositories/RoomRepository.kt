package fr.sercurio.soulseek.repositories

import fr.sercurio.soulseek.entities.RoomApiModel
import fr.sercurio.soulseek.entities.RoomMessageApiModel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object RoomRepository {
    private val roomsMutex = Mutex()
    private var rooms: List<RoomApiModel> = emptyList()

    suspend fun setRooms(rooms: List<RoomApiModel>) {
        roomsMutex.withLock {
            RoomRepository.rooms = rooms
        }
    }

    fun getRooms(): List<RoomApiModel> {
        return rooms
    }

    suspend fun addRoomMessage(roomMessageApiModel: RoomMessageApiModel) {
        roomsMutex.withLock {
            rooms.filter { it.name == roomMessageApiModel.room }
                .map { it.roomMessageApiModels.add(roomMessageApiModel) }
        }
    }
}