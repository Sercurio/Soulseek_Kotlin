package fr.sercurio.soulseek.rooms

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import fr.sercurio.soulseek.SoulseekApi
import fr.sercurio.soulseek.client.server.messages.RoomListMessage
import fr.sercurio.soulseek.client.server.messages.SayInRoomMessage

class RoomsViewModel(val soulseekApi: SoulseekApi) : ViewModel() {
    var roomsListState by mutableStateOf<RoomListMessage?>(null)
    var roomsMessagesState = mutableStateListOf<SayInRoomMessage?>()
    var currentRoomState = mutableStateOf<String?>(null)

    init {
        soulseekApi.onReceiveRoomList {
            roomsListState = it
        }

        soulseekApi.onSayInRoom { roomsMessagesState.add(it) }
    }

    suspend fun joinRoom(roomName: String) {
        soulseekApi.joinRoom(roomName)
    }

    suspend fun sayInRoom(roomName: String, message: String) {
        soulseekApi.sayInRoom(roomName, message)
    }
}