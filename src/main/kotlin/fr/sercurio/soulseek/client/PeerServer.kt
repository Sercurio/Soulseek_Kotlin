package withThreads

import java.net.ServerSocket
import java.net.Socket

class PeerServer(port: Int) {
    private var run = false
    private val serverSocket = ServerSocket(port)
    private lateinit var socket: Socket

    init {
        run = true
        while (run) {
            socket = serverSocket.accept()
            println("manconnected")
            val b = ByteArray(200)
            socket.getInputStream().read(b)
            socket.getOutputStream().write("salut".toByteArray())
            println(String(b))
        }
    }
}

