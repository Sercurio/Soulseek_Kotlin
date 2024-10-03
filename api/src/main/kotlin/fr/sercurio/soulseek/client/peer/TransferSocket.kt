package fr.sercurio.soulseek.client.peer

import fr.sercurio.soulseek.SoulInputStream
import fr.sercurio.soulseek.entities.ByteMessage
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import java.io.File
import java.nio.ByteBuffer
import kotlinx.coroutines.*

class TransferSocket(
    private val host: String,
    private val port: Int,
    private val token: Int,
    private val username: String,
) {
  private val tag = this::class.java.name

  private val selectorManager = ActorSelectorManager(Dispatchers.IO)
  private var socket: Socket? = null
  private var writeChannel: ByteWriteChannel? = null
  private lateinit var readChannel: SoulInputStream
  private val handler = CoroutineExceptionHandler { _, exception ->
    println("CoroutineExceptionHandler got $exception")
  }

  fun connect() {
    CoroutineScope(Dispatchers.IO).launch(handler) {
      launch {
            val socket = aSocket(selectorManager).tcp().connect(InetSocketAddress(host, port))
            this@TransferSocket.socket = socket
            this@TransferSocket.writeChannel = socket.openWriteChannel(autoFlush = true)
            this@TransferSocket.readChannel = SoulInputStream(socket.openReadChannel())
          }
          .join()
      onSocketConnected()
    }
  }

  private suspend fun send(message: ByteArray) {
    withContext(Dispatchers.IO) {
      val writeChannel =
          this@TransferSocket.writeChannel ?: throw IllegalStateException("Socket not connected")
      val buffer = ByteBuffer.wrap(message)
      writeChannel.writeFully(buffer)
    }
  }

  private suspend fun onSocketConnected() {
    try {
      println("downloading socket ready with $username")

      // pierceFirewall(peer.token)
      val fileSize = 6774597
      val file = File("output.mp3")
      if (file.exists()) file.delete()

      /*          val fileOutputStream = withContext(Dispatchers.IO) {
          FileOutputStream(file)
      }*/
      val buffer = ByteArray(131072)
      var nRead: Int
      var position = 0
      // val ticket = soulInput.readInt()

      // TODO this is here that we resume or not the download
      send(ByteMessage().writeInt32(0).getBuff())
      while (true) {
        // readChannel.readAndSetMessageLength()
        // println(readChannel.packLeft)
        // val code = readChannel.readInt()
        // println(code)
        withContext(Dispatchers.IO) {
          readChannel.byteReadChannel.toInputStream().read(buffer, 0, 1024)
        }
        println("yipee")
      }
      /* var transferring = true
                  while (transferring) {
                      nRead = readChannel.read(
                          buffer,
                          0,
                          if (fileSize - position < 131072) (fileSize - position) else 131072
                      )
                      if (nRead > 0) {
                          fileOutputStream.write(buffer, 0, nRead)
                          position += nRead
                          if (position == fileSize - 4) {
                              transferring = false
                          }
                      } else {
                          transferring = false
                      }
                      println(position)
                  }
                  println("finished")
                  println(buffer.toString())
      */
      /*            withContext(Dispatchers.IO) {
          fileOutputStream.close()
      }*/
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun onSocketDisconnected() {
    println("downloading socket closed")
  }

  private suspend fun pierceFirewall(token: Int) {
    send(ByteMessage().writeInt8(0).writeInt32(token).getBuff())
  }
}
