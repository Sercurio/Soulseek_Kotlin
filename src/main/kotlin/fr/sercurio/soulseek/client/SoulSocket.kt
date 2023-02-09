package fr.sercurio.soulseek.client

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers


abstract class SoulSocket(
    private val host: String, private val port: Int
) {
    private val tag = SoulSocket::class.java.name

    lateinit var readChannel: SoulInputStream
    private lateinit var writeChannel: ByteWriteChannel

    var connected = false

    suspend fun run() {
        try {
            val selectorManager = SelectorManager(Dispatchers.IO)
            val socket = aSocket(selectorManager).tcp().connect(host, port)
            readChannel = SoulInputStream(socket.openReadChannel())
            writeChannel = socket.openWriteChannel(autoFlush = true)

            onSocketConnected()
            connected = true

            while (connected) {

                onMessageReceived()
                readChannel.checkPackLeft()
            }
        } catch (e: Exception) {
            println(
                "Error + $e\n" +
                        "host:${host}\nport:${port}"
            )
            onSocketDisconnected()
        } finally {
            connected = false
        }
    }

    suspend fun sendMessage(message: ByteArray) {
        writeChannel.write { it.put(message) }
    }

    fun stop() {
        connected = false
    }

    abstract suspend fun onSocketConnected()
    abstract fun onSocketDisconnected()
    abstract fun onMessageReceived()
}
