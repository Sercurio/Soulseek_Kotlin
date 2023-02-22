package fr.sercurio.soulseek

import fr.sercurio.soulseek.client.ClientSoul
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


abstract class SoulSeekApi(
    login: String,
    password: String,
    listenPort: Int = 2000,
    host: String = "server.slsknet.org",
    port: Int = 2242
) {
    var clientSoul: ClientSoul

    init {
        clientSoul = ClientSoul(login, password, listenPort, host, port)
    }
}

suspend fun main() {
    runBlocking {
        val soulSeekApi = object : SoulSeekApi("DebugApp", "159753") {

        }

        delay(2000)
        /*
        soulSeekApi.clientSoul.userSearch(
            "Airmess",
            Random.nextInt(Integer.MAX_VALUE),
            "Stupeflip vite"
        )
         */
    }
}

