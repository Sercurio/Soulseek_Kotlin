package fr.sercurio.soulseek

import fr.sercurio.soulseek.client.ClientSoul
import fr.sercurio.soulseek.entities.PeerApiModel
import fr.sercurio.soulseek.entities.RoomApiModel
import fr.sercurio.soulseek.repositories.PeerRepository
import kotlin.random.Random
import kotlinx.coroutines.*

abstract class SoulseekApi(
    login: String,
    password: String,
    listenPort: Int = 2000,
    host: String = "server.slsknet.org",
    port: Int = 2242
) : SoulseekApiListener {
  var clientSoul: ClientSoul = ClientSoul(this, login, password, listenPort, host, port)
}

fun main() {
  runBlocking {
    val soulseekApi = object : SoulseekApi("DebugApp", "159753") {}

    delay(2000)
    soulseekApi.clientSoul.userSearch(
        "Airmess", Random.nextInt(Integer.MAX_VALUE), "Stupeflip vite")
  }
}

interface SoulseekApiListener {
  /* SERVER */
  fun onLogin(isConnected: Boolean, greeting: String?, nothing1: Int?, reason: String?) {}

  fun onGetPeerAddress(username: String, host: String, port: Int) {}

  fun onAddUser() {}

  fun onGetStatus() {}

  fun onSayInChatRoom(room: String, username: String, message: String) {}

  fun onJoinRoom() {}

  fun onLeaveRoom() {}

  fun onUserJoinedRoom(
      room: String,
      username: String,
      status: Int,
      avgspeed: Int,
      downloadNum: Long,
      files: Int,
      dirs: Int,
      slotsFree: Int,
      countryCode: String
  ) {}

  fun onUserLeftRoom(roomName: String, username: String) {}

  fun onConnectToPeer(username: String, type: String, ip: String, port: Int, token: Int) {
    CoroutineScope(Dispatchers.IO).launch {
      if (type == "P")
          PeerRepository.initiateClientSocket(
              this@SoulseekApiListener, PeerApiModel(username, type, ip, port, token))
      else if (type == "F")
          PeerRepository.initiateTransferSocket(
              this@SoulseekApiListener, PeerApiModel(username, type, ip, port, token))
    }
  }

  fun onPrivateMessages() {}

  fun onFileSearch() {}

  fun onPing() {}

  fun onKickedFromServer() {}

  fun onGetRecommendations() {}

  fun onGetGlobalRecommendations() {}

  fun onGetUserInterests() {}

  fun onRoomList(rooms: ArrayList<RoomApiModel>) {}

  fun onGlobalAdminMessage() {}

  fun onPrivilegedUsers() {}

  fun onAddPrivilegedUser() {}

  fun onCheckPrivileges() {}

  fun onSearchRequest() {}

  fun onNetInfo() {}

  fun onWishlistInterval() {}

  fun onGetSimilarUsers() {}

  fun onGetItemRecommendations() {}

  fun onGetItemSimilarUsers() {}

  fun onRoomTickers() {}

  fun onRoomTickerAdd() {}

  fun onRoomTickerRemove() {}

  fun onUserPrivileges() {}

  fun onAcknowledgeNotifyPrivileges() {}

  fun onPrivateRoomUsers() {}

  fun onPrivateRoomAddUser() {}

  fun onPrivateRoomRemoveUser() {}

  fun onPrivateRoomAdded() {}

  fun onPrivateRoomRemoved() {}

  fun onPrivateRoomToggle() {}

  fun onNewPassword() {}

  fun onPrivateRoomAddOperator() {}

  fun onPrivateRoomRemoveOperator() {}

  fun onPrivateRoomOperatorAdded() {}

  fun onPrivateRoomOperatorRemoved() {}

  fun onPrivateRoomOwned() {}

  fun onPublicChat() {}

  fun onCannotConnect() {}

  /* PEERS */
  fun onSharesRequest() {}

  fun onSharesReply() {}

  fun onPeerSearchRequest() {}

  fun onSearchReply(peer: PeerApiModel) {}

  fun onInfoRequest() {}

  fun onInfoReply() {}

  fun onFolderContentsRequest() {}

  fun onFolderContentsReply() {}

  fun onTransferRequest() {}

  fun onTransferReply() {}

  fun onQueueDownload() {}

  fun onPlaceInQueueReply() {}

  fun onUploadFailed() {}

  fun onQueueFailed() {}

  fun onPlaceInQueueRequest() {}

  fun onUploadQueueNotification() {}
}
