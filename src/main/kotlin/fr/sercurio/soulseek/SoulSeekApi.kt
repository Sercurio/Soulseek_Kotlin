package fr.sercurio.soulseek

import fr.sercurio.soulseek.client.ClientSoul
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random


class SoulSeekApi(login: String, password: String, listenPort: Int, host: String, port: Int) {
    var clientSoul: ClientSoul

    init {
        clientSoul = ClientSoul(login, password, listenPort, host, port)
    }
}

suspend fun main() {
    runBlocking {
        val soulSeekApi = SoulSeekApi("DebugApp", "159753", 2000, "server.slsknet.org", 2242)
        delay(2000)
        soulSeekApi.clientSoul.userSearch(
            "Airmess",
            Random.nextInt(Integer.MAX_VALUE),
            "Stupeflip vite"
        )
    }
}
