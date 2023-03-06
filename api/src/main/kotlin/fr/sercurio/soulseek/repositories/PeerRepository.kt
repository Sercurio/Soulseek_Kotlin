package fr.sercurio.soulseek.repositories

import fr.sercurio.soulseek.SoulseekApiListener
import fr.sercurio.soulseek.client.ClientPeer
import fr.sercurio.soulseek.client.TransferSocket
import fr.sercurio.soulseek.entities.PeerApiModel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object PeerRepository {
    private val peersMutex = Mutex()
    var peers: MutableMap<String, PeerApiModel> = mutableMapOf()

    suspend fun addOrUpdatePeer(peer: PeerApiModel) {
        peersMutex.withLock {
            peers[peer.username] = peer
        }
    }

    suspend fun initiateClientSocket(listener: SoulseekApiListener, peer: PeerApiModel) {
        peer.clientPeer = ClientPeer(listener, peer)
        peersMutex.withLock {
            peers[peer.username] = peer
        }
    }

    suspend fun initiateTransferSocket(listener: SoulseekApiListener, peer: PeerApiModel) {
        peer.transferSocket = TransferSocket(listener, peer)
        peersMutex.withLock {
            peers[peer.username] = peer
        }
    }
}