package fr.sercurio.soulseekapi.repositories

import fr.sercurio.soulseekapi.entities.PeerApiModel
import fr.sercurio.soulseekapi.socket.ClientSocket
import fr.sercurio.soulseekapi.socket.TransferSocket
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

    suspend fun initiateClientSocket(peer: PeerApiModel) {
        peer.clientSocket = ClientSocket(peer)
        peersMutex.withLock {
            peers[peer.username] = peer
        }
    }

    suspend fun initiateTransferSocket(peer: PeerApiModel) {
        peer.transferSocket = TransferSocket(peer)
        peersMutex.withLock {
            peers[peer.username] = peer
        }
    }
}