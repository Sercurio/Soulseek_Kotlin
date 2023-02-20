package fr.sercurio.saoul_seek.socket

import android.util.Log
import fr.sercurio.saoul_seek.message.ByteMessage
import fr.sercurio.saoul_seek.models.Peer
import fr.sercurio.saoul_seek.models.Room
import fr.sercurio.saoul_seek.models.RoomMessage
import fr.sercurio.saoul_seek.utils.Bytes
import java.security.NoSuchAlgorithmException
import kotlin.random.Random

class ServerClient private constructor(private val login: String, private val password: String, private val listenPort: Int, host: String, port: Int) : SoulSocket(host, port) {
    private val tag: String = this.javaClass.simpleName
    private lateinit var serverSocketInterface: ServerSocketInterface

    companion object {
        private var instance: ServerClient? = null

        fun getInstance(login: String, password: String, listenPort: Int, host: String, port: Int): ServerClient {
            if (instance == null)  // NOT thread safe!
                instance = ServerClient(login, password, listenPort, host, port)

            return instance!!
        }
    }

    override fun onMessageReceived() {
        synchronized(this) {
            this.packLeft = readInt()
            val code = readInt()
            //Log.d(tag, "Received: Message code:" + code + " Packet Size:" + (this.packLeft + 4))
            when (code) {
                1 -> receiveLogin()
                3 -> receiveGetPeerAddress()
                5 -> receiveAddUser()
                7 -> receiveGetStatus()
                13 -> receiveSayInChatRoom()
                14 -> receiveJoinRoom()
                15 -> receiveLeaveRoom()
                16 -> receiveUserJoinedRoom()
                17 -> receiveUserLeftRoom()
                18 -> receiveConnectToPeer()
                22 -> receivePrivateMessages()
                26 -> receiveFileSearch()
                32 -> receivePing()
                41 -> receiveKickedFromServer()
                54 -> receiveGetRecommendations()
                56 -> receiveGetGlobalRecommendations()
                57 -> receiveGetUserInterests()
                64 -> receiveRoomList()
                66 -> receiveGlobalAdminMessage()
                69 -> receivePrivilegedUsers()
                91 -> receiveAddPrivilegedUser()
                92 -> receiveCheckPrivileges()
                93 -> receiveSearchRequest()
                102 -> receiveNetInfo()
                104 -> receiveWishlistInterval()
                110 -> receiveGetSimilarUsers()
                111 -> receiveGetItemRecommendations()
                112 -> receiveGetItemSimilarUsers()
                113 -> receiveRoomTickers()
                114 -> receiveRoomTickerAdd()
                115 -> receiveRoomTickerRemove()
                122 -> receiveUserPrivileges()
                125 -> receiveAcknowledgeNotifyPrivileges()
                133 -> receivePrivateRoomUsers()
                134 -> receivePrivateRoomAddUser()
                135 -> receivePrivateRoomRemoveUser()
                139 -> receivePrivateRoomAdded()
                140 -> receivePrivateRoomRemoved()
                141 -> receivePrivateRoomToggle()
                142 -> receiveNewPassword()
                143 -> receivePrivateRoomAddOperator()
                144 -> receivePrivateRoomRemoveOperator()
                145 -> receivePrivateRoomOperatorAdded()
                146 -> receivePrivateRoomOperatorRemoved()
                148 -> receivePrivateRoomOwned()
                152 -> receivePublicChat()
                1001 -> receiveCannotConnect()
            }
        }
    }

    private fun receiveLogin() {
        if (readBoolean()) {
            val greeting = readString()
            val ip = readInt()
            Log.d(tag, "Logged In.")
            serverSocketInterface.onLogin(1, "connected", ip.toString())
        } else {
            val reason: String = readString()
            this.connected = false
            Log.d(tag, "Login Failed:$reason")
        }
    }

    private fun receiveGetPeerAddress() {
        val user = readString()
        val ip: String = readIp()
        val port = readInt()
        serverSocketInterface.onGetPeerAddress(user, ip, port)
    }


    private fun receiveAddUser() {
        val user = readString()
        if (readBoolean()) {
            val status = readInt()
            val avgSpeed = readInt()
            val donwloadNum: Long = readLong()
            val files = readInt()
            val dirs = readInt()
            readString()
        }
    }


    private fun receiveGetStatus() {
        val username = readString()
        val status = readInt()
        val privileged = readBoolean()
    }


    private fun receiveSayInChatRoom() {
        val room = readString()
        val username = readString()
        val message = readString()
        serverSocketInterface.onRoomMessage(room, username, message)
    }


    private fun receiveJoinRoom() {
        /*var i: Int
        val room = readString()
        val nUsers = readInt()
        val users = arrayOfNulls<String>(nUsers)
        i = 0
        while (i < nUsers) {
            users[i] = readString()
            i++
        }
        readInt()
        val status = IntArray(nUsers)
        i = 0
        while (i < nUsers) {
            status[i] = readInt()
            i++
        }
        readInt()
        val avgSpeed = IntArray(nUsers)
        val downloadNum = LongArray(nUsers)
        val files = IntArray(nUsers)
        val dirs = IntArray(nUsers)
        i = 0
        while (i < nUsers) {
            avgSpeed[i] = readInt()
            downloadNum[i] = readLong()
            files[i] = readInt()
            dirs[i] = readInt()
            i++
        }
        readInt()
        val slotsFree = IntArray(nUsers)
        i = 0
        while (i < nUsers) {
            slotsFree[i] = readInt()
            i++
        }
        readInt()
        val userCountries = arrayOfNulls<String>(nUsers)
        i = 0
        while (i < nUsers) {
            userCountries[i] = readString()
            i++
        }
        if (packLeft <= 0) {
            i = 0
            while (i < nUsers) {
                GoSeekData.newUserInRoom(users[i], room)
                i++
            }
            return
        }
        val owner = readString()
        val nOperators = readInt()
        val operators = arrayOfNulls<String>(nOperators)
        i = 0
        while (i < nOperators) {
            operators[i] = readString()
            i++
        }*/
    }


    private fun receiveLeaveRoom() {
        val room = readString()
    }


    private fun receiveUserJoinedRoom() {
        val room = readString()
        val username = readString()
        val status = readInt()
        val avgspeed = readInt()
        val downloadnum: Long = readLong()
        val files = readInt()
        val dirs = readInt()
        val slotsfree = readInt()
        val countrycode = readString()
        serverSocketInterface.onUserJoinRoom(room, username, status, avgspeed, downloadnum.toInt(), files, dirs, slotsfree, countrycode)
    }


    private fun receiveUserLeftRoom() {
        val roomName = readString()
        val username = readString()
        serverSocketInterface.onUserLeftRoom(roomName, username)
    }


    private fun receiveConnectToPeer() {
        val username = readString()
        val type = readString()
        val ip: String = readIp()
        val port = readInt()
        val token = readInt()
        readBoolean()
        //Log.d(tag, "Trying to connect as:$type To:$username")
        //Log.d(tag, "User Address: $ip:$port")
        //Log.d(tag, "Connection token:$token")
        serverSocketInterface.onConnectToPeer(Peer(username, type, ip, port, token.toLong()))
    }


    private fun receivePrivateMessages() {
        /*val ID = readInt()
        val timestamp = readInt()
        val username = readString()
        val message = readString()
        if (packLeft > 0) {
            val isAdmin = readBoolean()
        }
        sendAcknowledgePrivateMessage(ID)
        if (!GoSeekData.isUserIgnored(username)) {
            GoSeekData.newIncomingPrivateMessage(username, message, timestamp)
            val a: Activity = Util.uiActivity
            if (a != null && a.getClass() === PrivateMessageActivity::class.java) {
                (a as PrivateMessageActivity).requery(username)
            }
        }*/
        //TODO
    }


    private fun receiveFileSearch() {
        /*val username = readString()
        val ticket = readInt()
        val query = readString()
        val time = System.currentTimeMillis()
        onReceiveFileSearch(username, ticket, query, time)

        val cursor: Cursor = GoSeekData.searchShares(query)
        Log.d(tag, "Search Performed. query:" + query + " Time:" + (System.currentTimeMillis() - time))
        if (cursor != null) {
            Log.d(tag, "num results:" + cursor.getCount())
            if (cursor.getCount() !== 0) {
                this.service.sendToPeer(username, object : PeerMessage() {

                    private fun send(psock: PeerSocket) {
                        psock.sendSearchReply(ticket, query, cursor)
                    }
                })
            }
        }*/
    }

    private fun receivePing() {
        Log.d(tag, "ping from server.")
    }

    private fun receiveKickedFromServer() {
        /*Util.toast(this, "You were kicked from the server.")
        this.service.logout()*/
    }


    private fun receiveGetRecommendations() {
        val nRecs = readInt()
        val recs = arrayOfNulls<String>(nRecs)
        val recLevel = IntArray(nRecs)
        var i: Int = 0
        while (i < nRecs) {
            recs[i] = readString()
            recLevel[i] = readInt()
            i++
        }
        val nUnRecs = readInt()
        val unRecs = arrayOfNulls<String>(nUnRecs)
        val unRecLevel = IntArray(nUnRecs)
        i = 0
        while (i < nUnRecs) {
            unRecs[i] = readString()
            unRecLevel[i] = readInt()
            i++
        }
    }


    private fun receiveGetGlobalRecommendations() {
        var i: Int
        val nRecs = readInt()
        val recs = arrayOfNulls<String>(nRecs)
        val recLevel = IntArray(nRecs)
        i = 0
        while (i < nRecs) {
            recs[i] = readString()
            recLevel[i] = readInt()
            i++
        }
        val nUnRecs = readInt()
        val unRecs = arrayOfNulls<String>(nUnRecs)
        val unRecLevel = IntArray(nUnRecs)
        i = 0
        while (i < nUnRecs) {
            unRecs[i] = readString()
            unRecLevel[i] = readInt()
            i++
        }
    }


    private fun receiveGetUserInterests() {
        /*var i: Int
        val user = readString()
        val nLikes = readInt()
        var likes = String()
        i = 0
        while (i < nLikes) {
            likes = StringBuilder(likes).append(readString()).append("\n").toString()
            i++
        }
        val nDislikes = readInt()
        var dislikes = String()
        i = 0
        while (i < nDislikes) {
            dislikes = StringBuilder(dislikes).append(readString()).append("\n").toString()
            i++
        }
        val activity: Activity = Util.uiActivity
        if (activity.getClass() === ProfileActivity::class.java && (activity as ProfileActivity).peerName.equals(user)) {
            (activity as ProfileActivity).updateLikes(likes, dislikes)
        }*/
    }


    private fun receiveGlobalAdminMessage() {
        //Util.toast(this, "Admin Message: " + readString())
    }


    private fun receivePrivilegedUsers() {
        /*val nUsers = readInt()
        this.service.privilegedUsers.clear()
        for (i in 0 until nUsers) {
            this.service.privilegedUsers.add(readString())
        }
        Log.d(tag, "privileged users loaded")*/
    }


    private fun receiveAddPrivilegedUser() {
        //this.service.privilegedUsers.add(readString())
    }


    private fun receiveCheckPrivileges() {
        readInt()
    }


    private fun receiveSearchRequest() {
        val distributedCode: Byte = readByte()
        val unknown = readInt()
        val username = readString()
        val token = readInt()
        val query = readString()
    }


    private fun receiveNetInfo() {
        val nParents = readInt()
        val parentUser = arrayOfNulls<String>(nParents)
        val parentIp = arrayOfNulls<String>(nParents)
        val parentPort = IntArray(nParents)
        for (i in 0 until nParents) {
            parentUser[i] = readString()
            parentIp[i] = readIp()
            parentPort[i] = readInt()
        }
    }


    private fun receiveWishlistInterval() {
        val interval = readInt()
    }


    private fun receiveGetSimilarUsers() {
        val nUsers = readInt()
        val user = arrayOfNulls<String>(nUsers)
        val status = IntArray(nUsers)
        for (i in 0 until nUsers) {
            user[i] = readString()
            status[i] = readInt()
        }
    }


    private fun receiveGetItemRecommendations() {
        val item = readString()
        val nRecs = readInt()
        val recs = arrayOfNulls<String>(nRecs)
        val receivealues = IntArray(nRecs)
        for (i in 0 until nRecs) {
            recs[i] = readString()
            receivealues[i] = readInt()
        }
    }


    private fun receiveGetItemSimilarUsers() {
        val item = readString()
        val nUsers = readInt()
        val user = arrayOfNulls<String>(nUsers)
        for (i in 0 until nUsers) {
            user[i] = readString()
            readInt()
        }
    }


    private fun receiveRoomTickers() {
        /*var i: Int
        val room = readString()
        val nUsers = readInt()
        val user = arrayOfNulls<String>(nUsers)
        val ticker = arrayOfNulls<String>(nUsers)
        i = 0
        while (i < nUsers) {
            user[i] = readString()
            ticker[i] = readString()
            i++
        }
        GoSeekData.clearRoomTickers(room)
        i = 0
        while (i < nUsers) {
            GoSeekData.newTicker(room, user[i], ticker[i])
            i++
        }*/
    }


    private fun receiveRoomTickerAdd() {
        //GoSeekData.newTicker(readString(), readString(), readString())
    }


    private fun receiveRoomTickerRemove() {
        //GoSeekData.removeTicker(readString(), readString())
    }


    private fun receiveUserPrivileges() {
        /* val user = readString()
         if (readBoolean()) {
             if (!this.service.privilegedUsers.contains(user)) {
                 this.service.privilegedUsers.add(user)
             }
         } else if (this.service.privilegedUsers.contains(user)) {
             this.service.privilegedUsers.remove(user)
         }*/
    }


    private fun receiveAcknowledgeNotifyPrivileges() {
        val token = readInt()
    }


    private fun receivePrivateRoomUsers() {
        val room = readString()
        val nUsers = readInt()
        val users = arrayOfNulls<String>(nUsers)
        for (i in 0 until nUsers) {
            users[i] = readString()
        }
    }

    private fun receivePrivateRoomAddUser() {
        val room = readString()
        val user = readString()
    }

    private fun receivePrivateRoomRemoveUser() {
        val room = readString()
        val user = readString()
    }


    private fun receivePrivateRoomAdded() {
        val room = readString()
    }


    private fun receivePrivateRoomRemoved() {
        val room = readString()
    }


    private fun receivePrivateRoomToggle() {
        val inviteEnabled = readBoolean()
    }


    private fun receiveNewPassword() {
        /*val password = readString()
        Util.toast(this, "Password Successfully Changed.")*/
    }


    private fun receivePrivateRoomAddOperator() {
        val room = readString()
        val operator = readString()
    }


    private fun receivePrivateRoomRemoveOperator() {
        val room = readString()
        val operator = readString()
    }


    private fun receivePrivateRoomOperatorAdded() {
        val room = readString()
    }


    private fun receivePrivateRoomOperatorRemoved() {
        val room = readString()
    }


    private fun receivePrivateRoomOwned() {
        val room = readString()
        val nOperators = readInt()
        val operator = arrayOfNulls<String>(nOperators)
        for (i in 0 until nOperators) {
            operator[i] = readString()
        }
    }


    private fun receivePublicChat() {
        val room = readString()
        val user = readString()
        val message = readString()
    }


    private fun receiveCannotConnect() {
        val token = readInt()
        //onReceiveCannotConnect(token)
    }

    private fun login(login: String, pwd: String) {
        try {
            sendMessage(ByteMessage().writeInt32(1)
                    .writeStr(login)
                    .writeStr(pwd)
                    .writeInt32(160)
                    .writeStr(Bytes.md5(login + pwd))
                    .writeInt32(1)
                    .getBuff())

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    private fun setListenPort(port: Int) {
        sendMessage(ByteMessage()
                .writeInt32(2)
                .getBuff())
    }

    private fun getPeerAddressByUsername(username: String) {
        sendMessage(ByteMessage()
                .writeInt32(3)
                .writeStr(username)
                .getBuff())
    }

    private fun addUser(username: String) {
        sendMessage(ByteMessage()
                .writeInt32(5)
                .writeStr(username)
                .getBuff())
    }

    fun joinRoom(roomName: String /*Room room*/) {
        sendMessage(ByteMessage()
                .writeInt32(14)
                .writeStr(roomName /*room.getName()*/)
                .getBuff())
    }

    private fun setStatus(status: Int) {
        sendMessage(ByteMessage()
                .writeInt32(28)
                .writeInt32(status)
                .getBuff())
    }

    private fun sharedFoldersFiles(folderCount: Int, fileCount: Int) {
        sendMessage(ByteMessage()
                .writeInt32(26)
                .writeInt32(folderCount)
                .writeInt32(fileCount)
                .getBuff())
    }

    private fun haveNoParents(flag: Int) {
        sendMessage(ByteMessage()
                .writeInt32(71)
                .writeInt32(flag)
                .getBuff())
    }

    private fun parentIp(ip: IntArray) {
        sendMessage(ByteMessage()
                .writeInt32(73)
                .writeInt8(ip[0])
                .writeInt8(ip[1])
                .writeInt8(ip[2])
                .writeInt8(ip[3])
                .getBuff())
    }

    fun fileSearch(query: String, token: Int) {
        sendMessage(ByteMessage()
                .writeInt32(26)
                .writeInt32(Random.nextInt())
                //.writeRawBytes(Bytes.intToLittleEndian(token.toLong()))
                .writeStr(query)
                .getBuff())
    }

    fun sendRoomMessage(roomMessage: RoomMessage) {
        sendMessage(ByteMessage()
                .writeInt32(13)
                .writeStr(roomMessage.room)
                .writeStr(roomMessage.message)
                .getBuff())
    }

    private fun receiveRoomList() {
        val rooms = arrayListOf<Room>()

        val publicRooms = arrayListOf<Room>()
        val nbPublicRooms = readInt()
        for (j in 0 until nbPublicRooms) {
            publicRooms.add(Room(name = readString(), owner = false, private = false, operated = false))
        }
        for (room in publicRooms) {
            room.nbUsers = readInt()
        }

        rooms.addAll(publicRooms)

//        val ownedRooms = arrayListOf<Room>()
//        val nbOwnedRooms = readInt()
//        for (j in 0 until nbOwnedRooms) {
//            val roomName = readString()
//            ownedRooms.add(Room(name = roomName, owner = true, private = true, operated = false))
//        }
//        for (room in ownedRooms) {
//            val nbUsers = readInt()
//            room.nbUsers = nbUsers
//        }
//
//        rooms.addAll(ownedRooms)
//
//        val privateRooms = arrayListOf<Room>()
//        val nbPrivateRooms = readInt()
//        for (j in 0 until nbPrivateRooms) {
//            val roomName = readString()
//            privateRooms.add(Room(name = roomName, owner = false, private = true, operated = false))
//        }
//        for (room in privateRooms) {
//            val nbUsers = readInt()
//            room.nbUsers = nbUsers
//        }
//
//        rooms.addAll(privateRooms)
//
//        val operatedRooms = arrayListOf<Room>()
//        val nbOperatedRooms = readInt()
//        for (j in 0 until nbOperatedRooms) {
//            val roomName = readString()
//            operatedRooms.add(Room(name = roomName, owner = false, private = false, operated = true))
//        }
//        for (room in operatedRooms) {
//            val nbUsers = readInt()
//            room.nbUsers = nbUsers
//        }
//        rooms.addAll(operatedRooms)

        serverSocketInterface.onRoomList(rooms)
    }

    fun setServerSocketInterface(callback: ServerSocketInterface) {
        serverSocketInterface = callback
    }

    interface ServerSocketInterface {
        fun onLogin(connected: Int, greeting: String, ipAddress: String) {}
        fun onGetPeerAddress(username: String, ip: String, port: Int) {}
        fun onRoomMessage(roomName: String, username: String, message: String) {}
        private fun onJoinRoom(roomName: String, nbUsersRoom: Int) {}
        fun onUserJoinRoom(roomName: String, username: String, status: Int, averageSpeed: Int, downloadNum: Int, nbFiles: Int, nbDirectories: Int, slotsFree: Int, countryCode: String) {}
        fun onUserLeftRoom(roomName: String, username: String) {}
        fun onConnectToPeer(peer: Peer) {}
        fun onRoomList(rooms: ArrayList<Room>) {}
        private fun onPrivilegedUsersList() {}
        private fun onParentSpeedRatio() {}
        private fun onWishListInterval() {}
    }

    override fun onSocketConnected(soulSocket: SoulSocket) {
        soulSocket as ServerClient
        login(login, password)
        setListenPort(listenPort)
    }

    override fun onSocketDisconnected(exception: Exception) {
        Log.w(tag, "Disconnected from Server")
        //Log.i(tag, "Trying to reconnect...")
        //this@ServerSocket.init()
    }
}