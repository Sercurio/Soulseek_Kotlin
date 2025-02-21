package fr.sercurio.soulseek.client.shared

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteWriteChannel
import java.nio.ByteBuffer
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

abstract class AbstractSocket(private val host: String, private val port: Int) {
    private val connectedDeferred = CompletableDeferred<Boolean>()
    private val selectorManager = ActorSelectorManager(Dispatchers.IO)
    lateinit var socket: Socket
    private lateinit var writeChannel: ByteWriteChannel
    lateinit var readChannel: SoulInputStream

    private val writeMutex = Mutex()

    suspend fun connect() {
        withContext(Dispatchers.IO) {
                socket = aSocket(selectorManager).tcp().connect(InetSocketAddress(host, port))
                writeChannel = socket.openWriteChannel(autoFlush = true)
                readChannel = SoulInputStream(socket.openReadChannel())

                connectedDeferred.complete(true)

                onSocketConnected()

                while (isActive) {
                    whileConnected()
                }
        }
    }

    suspend fun send(message: ByteArray) {
        withContext(Dispatchers.IO) {
            connectedDeferred.await()

            writeMutex.withLock {
                val buffer = ByteBuffer.wrap(message)
                writeChannel.writeFully(buffer)
            }
        }
    }

    abstract suspend fun onSocketConnected()

    abstract suspend fun whileConnected()
}
