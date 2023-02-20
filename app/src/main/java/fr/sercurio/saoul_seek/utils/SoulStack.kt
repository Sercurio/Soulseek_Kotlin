package fr.sercurio.saoul_seek.utils

object SoulStack {
    var searches = HashMap<Int, String>()
    var actualSearchToken: Int = 0

    //var searches: HashMap<Long, String> = HashMap()
    //var search: String? = null

    //var search: ArrayList<String> = ArrayList()
    var download: ArrayList<String> = ArrayList()
    var downloadTokens: ArrayList<String> = ArrayList()
    var peerSearchMatches: ArrayList<String> = ArrayList()
    var peerSearchRequests: ArrayList<String> = ArrayList()
}