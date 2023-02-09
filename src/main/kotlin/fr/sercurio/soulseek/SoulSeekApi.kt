package fr.sercurio.soulseek

import fr.sercurio.soulseek.client.ServerClient

class SoulSeekApi(login: String, password: String, listenPort: Int, host: String, port: Int) {
    var serverClient: ServerClient

    init {
        serverClient = ServerClient(login, password, listenPort, host, port)
    }
}

fun main() {
    SoulSeekApi("Airmess", "159753", 2000, "server.slsknet.org", 2242)
}