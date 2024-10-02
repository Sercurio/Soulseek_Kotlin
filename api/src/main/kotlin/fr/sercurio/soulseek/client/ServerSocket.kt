package fr.sercurio.soulseek.client

import fr.sercurio.soulseek.entities.ByteMessage
import fr.sercurio.soulseek.toMD5
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.*

class ServerSocket(
    private val serverHost: String = "server.slsknet.org",
    private val serverPort: Int = 2242
) {
  val connected = AtomicBoolean(false)

  private val logger = KotlinLogging.logger {}
  private val selectorManager = ActorSelectorManager(Dispatchers.IO)
  private lateinit var socket: Socket
  private lateinit var writeChannel: ByteWriteChannel
  private lateinit var readChannel: SoulInputStream
  private var loginCallback: ((LoginResponse) -> Unit)? = null

  private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
    println("CoroutineExceptionHandler got $exception")
    connected.set(false)
  }

  init {
    CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
      launch { connect(serverHost, serverPort) }.join()
      connected.set(true)

      launch {
        while (true) {
          receive()
        }
      }
    }
  }

  private suspend fun connect(host: String, port: Int) {
    socket = aSocket(selectorManager).tcp().connect(InetSocketAddress(host, port))
    writeChannel = socket.openWriteChannel(autoFlush = true)
    readChannel = SoulInputStream(socket.openReadChannel())
  }

  suspend fun send(message: ByteArray) =
      withContext(Dispatchers.IO) {
        if (connected.get()) {
          val buffer = ByteBuffer.wrap(message)
          writeChannel.writeFully(buffer)
        } else {
          logger.error { "ServerSocket is not connected" }
        }
      }

  private suspend fun receive() {
    try {
      readChannel.readAndSetMessageLength()
      val code = readChannel.readInt()

      logger.info {
        "ServerClient received: Message code: $code Packet Size: ${readChannel.packLeft + 4}"
      }
      when (code) {
        1 -> onLogin()
      }
      //            3 -> receiveGetPeerAddress()
      //            5 -> receiveAddUser()
      //            7 -> receiveGetStatus()
      //            13 -> receiveSayInChatRoom()
      //            14 -> receiveJoinRoom()
      //            15 -> receiveLeaveRoom()
      //            16 -> receiveUserJoinedRoom()
      //            17 -> receiveUserLeftRoom()
      //            18 -> receiveConnectToPeer()
      //            22 -> receivePrivateMessages()
      //            26 -> receiveFileSearch()
      //            32 -> receivePing()
      //            41 -> receiveKickedFromServer()
      //            54 -> receiveGetRecommendations()
      //            56 -> receiveGetGlobalRecommendations()
      //            57 -> receiveGetUserInterests()
      //                  64 -> receiveRoomList()
      //            66 -> receiveGlobalAdminMessage()
      //            69 -> receivePrivilegedUsers()
      //            91 -> receiveAddPrivilegedUser()
      //            92 -> receiveCheckPrivileges()
      //            93 -> receiveSearchRequest()
      //            102 -> receiveNetInfo()
      //            104 -> receiveWishlistInterval()
      //            110 -> receiveGetSimilarUsers()
      //            111 -> receiveGetItemRecommendations()
      //            112 -> receiveGetItemSimilarUsers()
      //            113 -> receiveRoomTickers()
      //            114 -> receiveRoomTickerAdd()
      //            115 -> receiveRoomTickerRemove()
      //            122 -> receiveUserPrivileges()
      //            125 -> receiveAcknowledgeNotifyPrivileges()
      //            133 -> receivePrivateRoomUsers()
      //            134 -> receivePrivateRoomAddUser()
      //            135 -> receivePrivateRoomRemoveUser()
      //            139 -> receivePrivateRoomAdded()
      //            140 -> receivePrivateRoomRemoved()
      //            141 -> receivePrivateRoomToggle()
      //            142 -> receiveNewPassword()
      //            143 -> receivePrivateRoomAddOperator()
      //            144 -> receivePrivateRoomRemoveOperator()
      //            145 -> receivePrivateRoomOperatorAdded()
      //            146 -> receivePrivateRoomOperatorRemoved()
      //            148 -> receivePrivateRoomOwned()
      //            152 -> receivePublicChat()
      //            1001 -> receiveCannotConnect()
      readChannel.skipPackLeft()
    } catch (e: Exception) {
      throw e
    }
  }

  suspend fun login(login: String, pwd: String, callback: (LoginResponse) -> Unit) {
    loginCallback = callback

    send(
        ByteMessage()
            .writeInt32(1)
            .writeStr(login)
            .writeStr(pwd)
            .writeInt32(160)
            .writeStr((login + pwd).toMD5())
            .writeInt32(1)
            .getBuff())
  }

  private suspend fun onLogin() {
    if (readChannel.readBoolean()) {
      val greeting = readChannel.readString()
      val ip = readChannel.readInt()

      loginCallback?.invoke(LoginResponse(true, greeting, ip))
    } else {
      val reason: String = readChannel.readString()
      loginCallback?.invoke(LoginResponse(false, null, null, reason))
    }
  }
}
