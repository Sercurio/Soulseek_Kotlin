package fr.sercurio.soulseek.client

import fr.sercurio.soulseek.SoulseekApiListener
import fr.sercurio.soulseek.entities.ByteMessage
import fr.sercurio.soulseek.entities.PeerApiModel
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer


class TransferSocket(
    private val listener: SoulseekApiListener,
    private val peer: PeerApiModel,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val tag = this::class.java.name

    private val selectorManager = ActorSelectorManager(dispatcher)
    private var socket: io.ktor.network.sockets.Socket? = null
    private var writeChannel: ByteWriteChannel? = null
    private lateinit var readChannel: SoulInputStream

    init {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("CoroutineExceptionHandler got $exception")
        }
        CoroutineScope(Dispatchers.IO).launch(handler) {
            launch {
                connect()
            }.join()
            onSocketConnected()
        }
    }

    private suspend fun connect() {
        val socket = aSocket(selectorManager).tcp().connect(InetSocketAddress(peer.host, peer.port))
        this@TransferSocket.socket = socket
        this@TransferSocket.writeChannel = socket.openWriteChannel(autoFlush = true)
        this@TransferSocket.readChannel = SoulInputStream(socket.openReadChannel())
    }

    private suspend fun send(message: ByteArray) {
        withContext(dispatcher) {
            val writeChannel = this@TransferSocket.writeChannel ?: throw IllegalStateException("Socket not connected")
            val buffer = ByteBuffer.wrap(message)
            writeChannel.writeFully(buffer)
        }
    }

    private suspend fun onSocketConnected() {
        try {
            println("downloading socket ready")
            println(peer.toString())
            pierceFirewall(peer.token)
            val fileSize = 6774597
            val file = File("output.mp3")
            if (file.exists()) file.delete()
            val fileOutputStream = FileOutputStream(file)
            val buffer = ByteArray(131072)
            var nRead: Int
            var position = 0
            //val ticket = soulInput.readInt()

            //TODO this is here that we resume or not the download
            send(ByteMessage().writeInt32(0).getBuff())

            var transferring = true
            while (transferring) {/*
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
                */
            }
            println("finished")
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun onSocketDisconnected() {
        println("downloading socket closed")
    }

    private suspend fun pierceFirewall(token: Int) {
        send(
            ByteMessage().writeInt8(0).writeInt32(token).getBuff()
        )
    }
}