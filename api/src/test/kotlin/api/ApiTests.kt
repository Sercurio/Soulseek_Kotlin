package api

import fr.sercurio.soulseek.SoulseekApi
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
    private lateinit var soulSeekApi: SoulseekApi

    @BeforeAll
    fun initializeApi() {
    //    soulSeekApi = object : SoulseekApi("DebugApp", "159753"){}
    }

    @Test
    fun shouldSuccessLogin() {
        runBlocking {
            delay(2000)
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
            soulSeekApi.clientSoul.fileSearch("Shpongle")
            delay(5000)
            for (peer in PeerRepository.peers) {
                if (peer.value.soulFiles.isNotEmpty() && peer.value.slotsFree) {
                    peer.value.clientPeer?.queueUpload(peer.value.soulFiles[0])
                    break
                }
            }
            delay(2000)

            //assertTrue(PeerWaitingConnectRepository.getPeersWaitingConnect().isNotEmpty())
        }
    }

    @Test
    fun shouldReceiveConnectToPeerWith_P_Type() {
        runBlocking {
            soulSeekApi.clientSoul.userSearch(
                "Airmess", Random.nextInt(Integer.MAX_VALUE), "Stupeflip vite"
            )
            delay(1000)
            assertTrue(PeerRepository.peers["flat 58"]?.connectionType == "P")
        }
    }

    @Test
    fun shouldFindFilesOnThisUser() {
        runBlocking {
            soulSeekApi.clientSoul.userSearch(
                "asiku", Random.nextInt(Integer.MAX_VALUE), "Flute Fruit"
            )
            delay(2000)
            println(PeerRepository.peers)
            val user = PeerRepository.peers["asiku"]
            println(user?.soulFiles)
            user?.clientPeer?.queueUpload(user.soulFiles!![0])
        }
    }
}