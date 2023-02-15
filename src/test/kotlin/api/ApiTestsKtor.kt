package api

import fr.sercurio.soulseek.client.ClientSoul
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.lang.Exception
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiTestsKtor {

    @Test
    fun soulSocketKtor() {
        runBlocking {
            val clientSoul = ClientSoul("DebugApp", "159753", 4000, "server.slsknet.org", 2242)
            clientSoul.fileSearch("Shpongle")
            delay(5000)
        }
    }
}