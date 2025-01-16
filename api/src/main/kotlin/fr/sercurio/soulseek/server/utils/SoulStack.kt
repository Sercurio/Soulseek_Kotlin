package fr.sercurio.soulseek.server.utils

object SoulStack {
    var searches = HashMap<Int, String>()
    var actualSearchToken: Int = 0

    var download: ArrayList<String> = ArrayList()
    var downloadTokens: ArrayList<String> = ArrayList()
    var peerSearchMatches: ArrayList<String> = ArrayList()
    var peerSearchRequests: ArrayList<String> = ArrayList()
}
