package api

import fr.sercurio.soulseek.entities.ByteMessage
import fr.sercurio.soulseek.entities.LoginApiModel
import fr.sercurio.soulseek.repositories.LoginRepository
import fr.sercurio.soulseek.toMD5
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiTestsKtor {
    @Test
    fun ktorTests() {
        //"DebugApp", "159753", 4000, "server.slsknet.org", 2242)
        runBlocking {
            val selectorManager = SelectorManager(Dispatchers.IO)
            val socket = aSocket(selectorManager).tcp().connect("server.slsknet.org", 2242)

            val openReadChannel = socket.openReadChannel()
            val openWriteChannel = socket.openWriteChannel(autoFlush = true)


            val login = "Airmess"
            val pwd = "159753"
            openWriteChannel.write {
                it.put(
                    ByteMessage().writeInt32(1)
                        .writeStr(login)
                        .writeStr(pwd)
                        .writeInt32(160)
                        .writeStr((login + pwd).toMD5())
                        .writeInt32(1)
                        .getBuff()
                )
            }

            while (true) {
                val messageLength = openReadChannel.readIntLittleEndian()
                val code = openReadChannel.readIntLittleEndian()

                println("ServerClient received: Message code:" + code + " Packet Size:" + (messageLength + 4))
                when (code) {
                    1 -> {
                        if (openReadChannel.readBoolean()) {
                            val greetingLength = openReadChannel.readIntLittleEndian()
                            val greeting = ByteArray(greetingLength)
                            openReadChannel.readFully(greeting, 0, greetingLength)
                            val ip = openReadChannel.readIntLittleEndian()
                            println("Logged In.")
                            //serverSocketInterface.onLogin(1, "connected", ip.toString())

                            LoginRepository.setLoginStatus(LoginApiModel(true, ""))
                        } else {
                            val reasonLength = openReadChannel.readIntLittleEndian()
                            val reason = openReadChannel.readUTF8Line(reasonLength)
                            LoginRepository.setLoginStatus(LoginApiModel(false, ""))
                            println("Login Failed:$reason")
                        }
                    }

                    3 -> {}
                    5 -> {}
                    7 -> {}
                    13 -> {}
                    14 -> {}
                    15 -> {}
                    16 -> {}
                    17 -> {}
                    18 -> {}
                    22 -> {}
                    26 -> {}
                    32 -> {}
                    41 -> {}
                    54 -> {}
                    56 -> {}
                    57 -> {}
                    64 -> {}
                    66 -> {}
                    69 -> {}
                    91 -> {}
                    92 -> {}
                    93 -> {}
                    102 -> {}
                    104 -> {}
                    110 -> {}
                    111 -> {}
                    112 -> {}
                    113 -> {}
                    114 -> {}
                    115 -> {}
                    122 -> {}
                    125 -> {}
                    133 -> {}
                    134 -> {}
                    135 -> {}
                    139 -> {}
                    140 -> {}
                    141 -> {}
                    142 -> {}
                    143 -> {}
                    144 -> {}
                    145 -> {}
                    146 -> {}
                    148 -> {}
                    152 -> {}
                    1001 -> {}
                }

                openReadChannel.discardExact(messageLength.toLong() + 4)
            }
        }
    }
}