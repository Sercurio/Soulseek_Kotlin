package fr.sercurio.saoul_seek.models

import fr.sercurio.saoul_seek.socket.PeerSocket

data class Peer(var username: String,
                var connectionType: String,
                var host: String,
                var port: Int = 0,
                var token: Long = 0,
                var slotsFree: Boolean = true,
                var avgSpeed: Int = 0,
                var queueLength: Long = 0,
                var searchResults: Boolean = false,
                var socketPeer: PeerSocket? = null,
                var soulFiles: List<SoulFile>? = null) {

    override fun toString(): String {
        return "Peer{username='${username}', connectionType='${connectionType}', host='${host}', port=${port}, token='${token}', searchResult='${searchResults}, socketPeer='${socketPeer}}'"
    }
}