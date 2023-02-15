package fr.sercurio.soulseek

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

/*
class SoulSeekApi(login: String, password: String, listenPort: Int, host: String, port: Int) {
    var serverClient: ServerClient

    init {
        serverClient = ServerClient(login, password, listenPort, host, port)
    }
}

suspend fun main() {
    runBlocking {
        val soulSeekApi = SoulSeekApi("DebugApp", "159753", 2000, "server.slsknet.org", 2242)
        delay(2000)
        soulSeekApi.serverClient.userSearch(
            "Airmess",
            Random.nextInt(Integer.MAX_VALUE),
            "Stupeflip vite"
        )
    }
}
 */