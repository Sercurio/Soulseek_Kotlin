package fr.sercurio.saoul_seek.socket

import android.util.Log
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
            Log.d("ManConnected", "manconnected")
            val b = ByteArray(200)
            socket.getInputStream().read(b)
            socket.getOutputStream().write("salut".toByteArray())
            Log.d("IP", String(b))
        }
    }
}

