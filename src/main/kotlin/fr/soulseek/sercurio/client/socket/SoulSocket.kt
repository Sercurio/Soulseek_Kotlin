package fr.sercurio.soulseekapi.socket

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import withThreads.SoulInputStream
import java.io.DataInputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors


abstract class SoulSocket(
    private val host: String, private val port: Int
) : Runnable {
    private val tag = SoulSocket::class.java.name

    private val executorService = Executors.newSingleThreadExecutor()

    lateinit var soulInput: SoulInputStream
    private lateinit var output: OutputStream

    var connected = false

    override fun run() {
        val socket = Socket()
        try {
            socket.connect(InetSocketAddress(host, port), 1000)
            val mIn: InputStream = socket.getInputStream()
            output = socket.getOutputStream()
            soulInput = SoulInputStream(DataInputStream(mIn))

            onSocketConnected()
            connected = true

            while (connected) {

                onMessageReceived()
                soulInput.checkPackLeft()
            }
        } catch (e: Exception) {
            println(
                "Error + $e\n" +
                        "host:${host}\nport:${port}"
            )
            onSocketDisconnected()
        } finally {
            connected = false
            socket.close()
        }

    }

    fun sendMessage(message: ByteArray) {
        executorService.submit {
            output.write(message)
            output.flush()
        }
    }

    fun stop() {
        connected = false
    }

    abstract fun onSocketConnected()
    abstract fun onSocketDisconnected()
    abstract fun onMessageReceived()
}
