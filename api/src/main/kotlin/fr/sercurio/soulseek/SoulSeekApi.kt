package fr.sercurio.soulseek

import fr.sercurio.soulseek.client.ClientSoul
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random


class SoulSeekApi(
    login: String,
    password: String,
    listenPort: Int = 2000,
    host: String = "server.slsknet.org",
    port: Int = 2242
) {
    var clientSoul: ClientSoul

    init {
        println("$login $password, $listenPort, $host, $port")
        clientSoul = ClientSoul(login, password, listenPort, host, port)
    }

    open fun onLogin() {

    }
}

suspend fun main() {
    runBlocking {
        val soulSeekApi = SoulSeekApi("DebugApp", "159753")
        delay(2000)
        soulSeekApi.clientSoul.userSearch(
            "Airmess",
            Random.nextInt(Integer.MAX_VALUE),
            "Stupeflip vite"
        )
    }
}
