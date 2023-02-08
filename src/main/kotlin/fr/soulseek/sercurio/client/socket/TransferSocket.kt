package fr.sercurio.soulseekapi.socket

import fr.sercurio.soulseekapi.entities.ByteMessage
import fr.sercurio.soulseekapi.entities.PeerApiModel
import withThreads.SoulInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors


class TransferSocket(private val peer: PeerApiModel) : Runnable {
    private val tag = SoulSocket::class.java.name
    private lateinit var output: OutputStream
    private val executorService = Executors.newSingleThreadExecutor()

    private var connected = false

    init {
        Thread(this).start()
    }

    override fun run() {
        val socket = Socket()
        try {
            socket.connect(InetSocketAddress(peer.host, peer.port), 1000)
            output = socket.getOutputStream()
            val inputStream = socket.getInputStream()
            val soulInput = SoulInputStream(DataInputStream(inputStream))

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
                    val ticket = soulInput.readInt()

                    //TODO this is here that we resume or not the download
                    sendMessage(ByteMessage().writeInt32(0).getBuff())

                    var transferring = true
                    while (transferring) {
                        nRead = inputStream.read(
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
            socket.close()
        }
    }

    private fun onSocketConnected() {
        println("downloading socket ready")
    }

    private fun onSocketDisconnected() {
        println("downloading socket closed")
    }

    private fun sendMessage(message: ByteArray) {
        executorService.submit {
            output.write(message)
            output.flush()
        }
    }

    private fun pierceFirewall(token: Int) {
        sendMessage(
            ByteMessage()
                .writeInt8(0)
                .writeInt32(token)
                .getBuff()
        )
    }
}