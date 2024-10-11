package fr.sercurio.soulseek.client

import fr.sercurio.soulseek.SoulInputStream
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.close
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

abstract class AbstractSocket(
    private val host: String,
    private val port: Int,
) {
    private val connectedDeferred = CompletableDeferred<Boolean>()
    private val selectorManager = ActorSelectorManager(Dispatchers.IO)
    lateinit var socket: Socket
    private lateinit var writeChannel: ByteWriteChannel
    lateinit var readChannel: SoulInputStream

    private val writeMutex = Mutex()

    suspend fun connect() {
        withContext(Dispatchers.IO) {
            try {
                socket = aSocket(selectorManager).tcp().connect(InetSocketAddress(host, port))
                writeChannel = socket.openWriteChannel(autoFlush = true)
                readChannel = SoulInputStream(socket.openReadChannel())

                connectedDeferred.complete(true)

                onSocketConnected()

                while (isActive) {
                    whileConnected()
                }
            } catch (e: Exception) {
                println("$e")
            } finally {
//                    socket.close()
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