package fr.sercurio.soulseek.entities

import fr.sercurio.soulseek.client.peer.PeerSocket
import fr.sercurio.soulseek.client.peer.TransferSocket

data class PeerApiModel(
    var username: String,
    var connectionType: String = "",
    var host: String,
    var port: Int = 0,
    var token: Int = 0,
    var slotsFree: Boolean = true,
    var avgSpeed: Int = 0,
    var queueLength: Long = 0,
    var peerSocket: PeerSocket? = null,
    var transferSocket: TransferSocket? = null,
    var soulFiles: List<SoulFile> = emptyList()
) {
    override fun toString(): String {
        return "PeerApiModel(username='$username', connectionType='$connectionType', host='$host', port=$port, token=$token, slotsFree=$slotsFree, avgSpeed=$avgSpeed, queueLength=$queueLength, clientSocket=$peerSocket, transferSocket=$transferSocket, soulFiles=$soulFiles)"
    }
}