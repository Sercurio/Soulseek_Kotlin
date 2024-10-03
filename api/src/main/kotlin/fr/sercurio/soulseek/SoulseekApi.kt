package fr.sercurio.soulseek

import fr.sercurio.soulseek.client.peer.PeerSocket
import fr.sercurio.soulseek.client.peer.TransferSocket
import fr.sercurio.soulseek.client.server.ServerSocket
import fr.sercurio.soulseek.client.server.messages.LoginMessage
import fr.sercurio.soulseek.client.server.messages.RoomListMessage
import fr.sercurio.soulseek.entities.PeerApiModel
import fr.sercurio.soulseek.entities.RoomApiModel
import kotlinx.coroutines.*

class SoulseekApi(
    login: String,
    password: String,
    listenPort: Int = 2000,
    host: String = "server.slsknet.org",
    port: Int = 2242,
) {
  private val serverSocket: ServerSocket = ServerSocket(login, password, listenPort, host, port)

  init {
    serverSocket.onReceiveConnectToPeer {
      CoroutineScope(Dispatchers.IO).launch {
        if (it.type == "P") {
          val peer = PeerSocket(it.ip, it.port, it.token, it.username)
          peer.onReceiveSearchReply {
            runBlocking {
              println("asking for the file " + it.soulFiles[0].path)
              peer.queueUpload(it.soulFiles[0].path)
            }
          }

          peer.connect()
        } else if (it.type == "F") {
          TransferSocket(it.ip, it.port, it.token, it.username).connect()
        }
      }
    }
  }

  fun login() {
    serverSocket.connect()
  }

  fun onLogin(callback: (LoginMessage) -> Unit) {
    serverSocket.onLogin { callback(it) }
  }

  fun onReceiveRoomList(callback: (RoomListMessage) -> Unit) {
    serverSocket.onReceiveRoomList { callback(it) }
  }

  suspend fun fileSearch(query: String) {
    serverSocket.fileSearch(query)
  }
}

fun main() {
  runBlocking {
    val api = SoulseekApi("Airmess", "159753")

    api.onLogin { if (it.connected) println("Logged sucessfully !") else println("not logged") }
    api.onReceiveRoomList { println(it.rooms) }

    api.login()

    api.fileSearch("La danse des canards")

    while (true) {}

    //    delay(2000)
    //    soulseekApi.clientSoul.userSearch(
    //        "Airmess", Random.nextInt(Integer.MAX_VALUE), "Stupeflip vite")
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

  //  fun onConnectToPeer(username: String, type: String, ip: String, port: Int, token: Int) {
  //    CoroutineScope(Dispatchers.IO).launch {
  //      if (type == "P")
  //          PeerRepository.initiateClientSocket(
  //              this@SoulseekApiListener, PeerApiModel(username, type, ip, port, token))
  //      else if (type == "F")
  //          PeerRepository.initiateTransferSocket(
  //              this@SoulseekApiListener, PeerApiModel(username, type, ip, port, token))
  //    }
  //  }

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
