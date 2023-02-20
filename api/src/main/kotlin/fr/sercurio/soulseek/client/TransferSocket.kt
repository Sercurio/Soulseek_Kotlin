package fr.sercurio.soulseek.client

import fr.sercurio.soulseek.entities.ByteMessage
import fr.sercurio.soulseek.entities.PeerApiModel
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors


class TransferSocket(private val peer: PeerApiModel) {
    private val tag = SoulSocket::class.java.name

    private lateinit var readChannel: SoulInputStream
    private lateinit var writeChannel: ByteWriteChannel

    private var connected = false

    suspend operator fun invoke() {
        try {
            val selectorManager = SelectorManager(Dispatchers.IO)
            val socket = aSocket(selectorManager).tcp().connect(peer.host, peer.port)
            readChannel = SoulInputStream(socket.openReadChannel())
            writeChannel = socket.openWriteChannel()

            onSocketConnected()

            connected = true
            if (connected) {
                try {
                    println(peer.toString())
                    pierceFirewall(peer.token)
                    val fileSize = 6774597
                    val file = File("output.mp3")
                    if (file.exists())
                        file.delete()
                    val fileOutputStream = FileOutputStream(file)
                    val buffer = ByteArray(131072)
                    var nRead: Int
                    var position = 0
                    //val ticket = soulInput.readInt()

                    //TODO this is here that we resume or not the download
                    sendMessage(ByteMessage().writeInt32(0).getBuff())

                    var transferring = true
                    while (transferring) {
                        /*
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
        } catch (e: Exception) {
            println(
                "Error + $e\n" +
                        "host:${peer.host}\nport:${
                            peer.port
                        }"
            )
            onSocketDisconnected()
        } finally {
            connected = false
        }
    }

    private fun onSocketConnected() {
        println("downloading socket ready")
    }

    private fun onSocketDisconnected() {
        println("downloading socket closed")
    }

    private suspend fun sendMessage(message: ByteArray) {
        writeChannel.write { it.put(message) }
    }

    private suspend fun pierceFirewall(token: Int) {
        sendMessage(
            ByteMessage()
                .writeInt8(0)
                .writeInt32(token)
                .getBuff()
        )
    }
}