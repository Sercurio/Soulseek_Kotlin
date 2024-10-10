package api

import fr.sercurio.soulseek.SoulseekApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiTests {
    private val soulSeekApi = SoulseekApi()

    @Test
    fun shouldSuccessLogin() = runTest {
        soulSeekApi.onLogin { assertTrue(it.connected) }
        soulSeekApi.login("login", "password")
    }

    @Test
    fun shouldReceiveRooms() = runTest {
        soulSeekApi.onReceiveRoomList { assertTrue(it.rooms.isNotEmpty()) }
        soulSeekApi.login("login", "password")
    }
    //
    //  @Test
    //  fun shouldPeersTryToConnectUs() {
    //    runBlocking {
    //      soulSeekApi.clientSoul.fileSearch("Shpongle")
    //      delay(5000)
    //      for (peer in PeerRepository.peers) {
    //        if (peer.value.soulFiles.isNotEmpty() && peer.value.slotsFree) {
    //          peer.value.peerSocket?.queueUpload(peer.value.soulFiles[0].filename)
    //          break
    //        }
    //      }
    //      delay(2000)
    //
    //      // assertTrue(PeerWaitingConnectRepository.getPeersWaitingConnect().isNotEmpty())
    //    }
    //  }
    //
    //  @Test
    //  fun shouldReceiveConnectToPeerWith_P_Type() {
    //    runBlocking {
    //      soulSeekApi.clientSoul.userSearch(
    //          "login", Random.nextInt(Integer.MAX_VALUE), "a track name")
    //      delay(1000)
    //      assertTrue(PeerRepository.peers["flat 58"]?.connectionType == "P")
    //    }
    //  }
}
