package fr.sercurio.soulseekapi.entities

import fr.sercurio.soulseekapi.socket.ClientSocket
import fr.sercurio.soulseekapi.socket.TransferSocket

data class PeerApiModel(
    var username: String,
    var connectionType: String = "",
    var host: String,
    var port: Int = 0,
    var token: Int = 0,
    var slotsFree: Boolean = true,
    var avgSpeed: Int = 0,
    var queueLength: Long = 0,
    var clientSocket: ClientSocket? = null,
    var transferSocket: TransferSocket? = null,
    var soulFiles: List<SoulFile>? = null
) {
    override fun toString(): String {
        return "PeerApiModel(username='$username', connectionType='$connectionType', host='$host', port=$port, token=$token, slotsFree=$slotsFree, avgSpeed=$avgSpeed, queueLength=$queueLength, clientSocket=$clientSocket, transferSocket=$transferSocket, soulFiles=$soulFiles)"
    }
}