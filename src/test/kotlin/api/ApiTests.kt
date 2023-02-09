package api

import fr.sercurio.soulseek.SoulSeekApi
import fr.sercurio.soulseek.repositories.LoginRepository
import fr.sercurio.soulseek.repositories.PeerRepository
import fr.sercurio.soulseek.repositories.RoomRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.random.Random

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiTests {
    private lateinit var soulSeekApi: SoulSeekApi

    @BeforeAll
    fun initializeApi() {
        soulSeekApi = SoulSeekApi("DebugApp", "159753", 4000, "server.slsknet.org", 2242)
        Thread.sleep(1000)
    }

    @Test
    fun shouldSuccessLogin() {
        runBlocking {
            assertEquals(true, LoginRepository.getLoginStatus().connected)
        }
    }

    @Test
    fun shouldReceiveRooms() {
        runBlocking {
            assertTrue(RoomRepository.getRooms().isNotEmpty())
        }
    }

    @Test
    fun shouldPeersTryToConnectUs() {
        runBlocking {
            soulSeekApi.serverClient.fileSearch("Shpongle")
            delay(1000)
            //assertTrue(PeerWaitingConnectRepository.getPeersWaitingConnect().isNotEmpty())
        }
    }

    @Test
    fun shouldReceiveConnectToPeerWith_P_Type() {
        runBlocking {
            soulSeekApi.serverClient.userSearch(
                "flat 58",
                Random.nextInt(Integer.MAX_VALUE),
                "soupe au choux"
            )
            delay(1000)
            assertTrue(PeerRepository.peers["flat 58"]?.connectionType == "P")
        }
    }

    @Test
    fun shouldFindFilesOnThisUser() {
        runBlocking {
            soulSeekApi.serverClient.userSearch(
                "asiku",
                Random.nextInt(Integer.MAX_VALUE),
                "Flute Fruit"
            )
            delay(2000)
            println(PeerRepository.peers)
            val user = PeerRepository.peers["asiku"]
            println(user?.soulFiles)
            user
                ?.clientSocket
                ?.transferRequest(0, user.token, user.soulFiles!![0], null)
            while (true);
        }
    }
}