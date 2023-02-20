package fr.sercurio.soulseek.client

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer


abstract class SoulSocket(
    private val host: String, private val port: Int, private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val tag = SoulSocket::class.java.name

    private val selectorManager = ActorSelectorManager(dispatcher)
    private var socket: Socket? = null
    private var writeChannel: ByteWriteChannel? = null
    lateinit var readChannel: SoulInputStream

    suspend fun connect() {
        withContext(dispatcher) {
            val socket = aSocket(selectorManager).tcp().connect(InetSocketAddress(host, port))
            this@SoulSocket.socket = socket
            this@SoulSocket.writeChannel = socket.openWriteChannel(autoFlush = true)
            this@SoulSocket.readChannel = SoulInputStream(socket.openReadChannel())
            onSocketConnected()
        }
    }

    suspend fun receive() {
        onMessageReceived()
    }

    suspend fun send(message: ByteArray) {
        withContext(dispatcher) {
            val writeChannel = this@SoulSocket.writeChannel ?: throw IllegalStateException("Socket not connected")
            val buffer = ByteBuffer.wrap(message)
            writeChannel.writeFully(buffer)
        }
    }

    fun close() {
        socket?.close()
    }

    abstract suspend fun onSocketConnected()
    abstract fun onSocketDisconnected()
    abstract suspend fun onMessageReceived()
}