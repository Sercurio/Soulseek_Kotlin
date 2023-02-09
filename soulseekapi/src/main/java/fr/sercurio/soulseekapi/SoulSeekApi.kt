package fr.sercurio.soulseekapi

import fr.sercurio.soulseekapi.socket.ServerClient

class SoulSeekApi(login: String, password: String, listenPort: Int, host: String, port: Int) {
    var serverClient: ServerClient =
        ServerClient.getInstance(login, password, listenPort, host, port)

    init {
        val soulServerThread = Thread(serverClient)
        soulServerThread.start()
    }
}