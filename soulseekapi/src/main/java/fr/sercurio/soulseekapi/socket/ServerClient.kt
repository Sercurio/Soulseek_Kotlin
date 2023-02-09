package fr.sercurio.soulseekapi.socket


import fr.sercurio.soulseekapi.entities.*
import fr.sercurio.soulseekapi.repositories.LoginRepository
import fr.sercurio.soulseekapi.repositories.PeerRepository
import fr.sercurio.soulseekapi.repositories.RoomRepository
import fr.sercurio.soulseekapi.utils.Hash
import kotlinx.coroutines.runBlocking
import fr.sercurio.soulseekapi.utils.SoulStack
import kotlin.random.Random

class ServerClient private constructor(
    private val login: String,
    private val password: String,
    private val listenPort: Int,
    host: String,
    port: Int
) : SoulSocket(host, port) {
    companion object {
        private var instance: ServerClient? = null

        fun getInstance(
            login: String,
            password: String,
            listenPort: Int,
            host: String,
            port: Int
        ): ServerClient {
            if (instance == null)  // NOT thread safe!
                instance = ServerClient(login, password, listenPort, host, port)

            return instance!!
        }
    }

    override fun onSocketConnected() {
        login(login, password)
        setListenPort(listenPort)
    }

    override fun onSocketDisconnected() {
        println("Disconnected from Server")
    }

    override fun onMessageReceived() {
        runBlocking {
            soulInput.readAndSetMessageLength()

            //this.packLeft = soulInput.readInt()
            val code = soulInput.readInt()
            println("ServerClient received: Message code:" + code + " Packet Size:" + (soulInput.packLeft + 4))
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


    private suspend fun receiveLogin() {
        if (soulInput.readBoolean()) {
            val greeting = soulInput.readString()
            val ip = soulInput.readInt()
            println("Logged In.")
            //serverSocketInterface.onLogin(1, "connected", ip.toString())

            LoginRepository.setLoginStatus(LoginApiModel(true, ""))

        } else {
            val reason: String = soulInput.readString()
            this.connected = false
            println("Login Failed:$reason")
        }
    }

    private suspend fun receiveGetPeerAddress() {
        val username = soulInput.readString()
        val ip: String = soulInput.readIp()
        val port = soulInput.readInt()

        PeerRepository.addOrUpdatePeer(PeerApiModel(username = username, host = ip, port = port))
    }


    private fun receiveAddUser() {
        val user = soulInput.readString()
        if (soulInput.readBoolean()) {
            val status = soulInput.readInt()
            val avgSpeed = soulInput.readInt()
            val downloadNum: Long = soulInput.readLong()
            val files = soulInput.readInt()
            val dirs = soulInput.readInt()
            soulInput.readString()
        }
    }


    private fun receiveGetStatus() {
        val username = soulInput.readString()
        val status = soulInput.readInt()
        val privileged = soulInput.readBoolean()
    }


    private suspend fun receiveSayInChatRoom() {
        val room = soulInput.readString()
        val username = soulInput.readString()
        val message = soulInput.readString()
        RoomRepository.addRoomMessage(RoomMessageApiModel(room, username, message))
    }


    private fun receiveJoinRoom() {
        /*var i: Int
        val room = soulInput.readString()
        val nUsers = soulInput.readInt()
        val users = arrayOfNulls<String>(nUsers)
        i = 0
        while (i < nUsers) {
            users[i] = soulInput.readString()
            i++
        }
        soulInput.readInt()
        val status = IntArray(nUsers)
        i = 0
        while (i < nUsers) {
            status[i] = soulInput.readInt()
            i++
        }
        soulInput.readInt()
        val avgSpeed = IntArray(nUsers)
        val downloadNum = LongArray(nUsers)
        val files = IntArray(nUsers)
        val dirs = IntArray(nUsers)
        i = 0
        while (i < nUsers) {
            avgSpeed[i] = soulInput.readInt()
            downloadNum[i] = readLong()
            files[i] = soulInput.readInt()
            dirs[i] = soulInput.readInt()
            i++
        }
        soulInput.readInt()
        val slotsFree = IntArray(nUsers)
        i = 0
        while (i < nUsers) {
            slotsFree[i] = soulInput.readInt()
            i++
        }
        soulInput.readInt()
        val userCountries = arrayOfNulls<String>(nUsers)
        i = 0
        while (i < nUsers) {
            userCountries[i] = soulInput.readString()
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
        val owner = soulInput.readString()
        val nOperators = soulInput.readInt()
        val operators = arrayOfNulls<String>(nOperators)
        i = 0
        while (i < nOperators) {
            operators[i] = soulInput.readString()
            i++
        }*/
    }


    private suspend fun receiveLeaveRoom() {
        val room = soulInput.readString()
        RoomRepository.addRoomMessage(RoomMessageApiModel(room, "SYSTEM", "Leaving room"))
    }


    private suspend fun receiveUserJoinedRoom() {
        val room = soulInput.readString()
        val username = soulInput.readString()
        val status = soulInput.readInt()
        val avgspeed = soulInput.readInt()
        val downloadNum: Long = soulInput.readLong()
        val files = soulInput.readInt()
        val dirs = soulInput.readInt()
        val slotsFree = soulInput.readInt()
        val countryCode = soulInput.readString()

        RoomRepository.addRoomMessage(
            RoomMessageApiModel(
                room,
                "SYSTEM",
                "$username has joined the room."
            )
        )
    }


    private suspend fun receiveUserLeftRoom() {
        val roomName = soulInput.readString()
        val username = soulInput.readString()

        RoomRepository.addRoomMessage(
            RoomMessageApiModel(
                roomName,
                "SYSTEM",
                "$username has left the room."
            )
        )
    }


    private suspend fun receiveConnectToPeer() {
        val username = soulInput.readString()
        val type = soulInput.readString()
        val ip: String = soulInput.readIp()
        val port = soulInput.readInt()
        val token = soulInput.readInt()
        soulInput.readBoolean()

        if (type == "P")
            PeerRepository.initiateClientSocket(
                PeerApiModel(
                    username,
                    type,
                    ip,
                    port,
                    token
                )
            ) else if (type == "F")
            PeerRepository.initiateTransferSocket(
                PeerApiModel(
                    username,
                    type,
                    ip,
                    port,
                    token
                )
            )

    }

    private fun receivePrivateMessages() {
        /*val ID = soulInput.readInt()
        val timestamp = soulInput.readInt()
        val username = soulInput.readString()
        val message = soulInput.readString()
        if (packLeft > 0) {
            val isAdmin = soulInput.readBoolean()
        }
        sendAcknowledgePrivateMessage(ID)
        if (!GoSeekData.isUserIgnored(username)) {
            GoSeekData.newIncomingPrivateMessage(username, message, timestamp)
            val a: Activity = Util.uiActivity
            if (a != null && a.getClass() === PrivateMessageActivity::class.java) {
                (a as PrivateMessageActivity).requery(username)
            }
        }*/
    }


    private fun receiveFileSearch() {
        /*val username = soulInput.readString()
        val ticket = soulInput.readInt()
        val query = soulInput.readString()
        val time = System.currentTimeMillis()
        onReceiveFileSearch(username, ticket, query, time)

        val cursor: Cursor = GoSeekData.searchShares(query)
        println( "Search Performed. query:" + query + " Time:" + (System.currentTimeMillis() - time))
        if (cursor != null) {
            println( "num results:" + cursor.getCount())
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
        println("ping from server.")
    }

    private fun receiveKickedFromServer() {
        /*Util.toast(this, "You were kicked from the server.")
        this.service.logout()*/
    }


    private fun receiveGetRecommendations() {
        val nRecs = soulInput.readInt()
        val recs = arrayOfNulls<String>(nRecs)
        val recLevel = IntArray(nRecs)
        var i: Int = 0
        while (i < nRecs) {
            recs[i] = soulInput.readString()
            recLevel[i] = soulInput.readInt()
            i++
        }
        val nUnRecs = soulInput.readInt()
        val unRecs = arrayOfNulls<String>(nUnRecs)
        val unRecLevel = IntArray(nUnRecs)
        i = 0
        while (i < nUnRecs) {
            unRecs[i] = soulInput.readString()
            unRecLevel[i] = soulInput.readInt()
            i++
        }
    }


    private fun receiveGetGlobalRecommendations() {
        val nRecs = soulInput.readInt()
        val recs = arrayOfNulls<String>(nRecs)
        val recLevel = IntArray(nRecs)
        var i: Int = 0
        while (i < nRecs) {
            recs[i] = soulInput.readString()
            recLevel[i] = soulInput.readInt()
            i++
        }
        val nUnRecs = soulInput.readInt()
        val unRecs = arrayOfNulls<String>(nUnRecs)
        val unRecLevel = IntArray(nUnRecs)
        i = 0
        while (i < nUnRecs) {
            unRecs[i] = soulInput.readString()
            unRecLevel[i] = soulInput.readInt()
            i++
        }
    }


    private fun receiveGetUserInterests() {
        /*var i: Int
        val user = soulInput.readString()
        val nLikes = soulInput.readInt()
        var likes = String()
        i = 0
        while (i < nLikes) {
            likes = StringBuilder(likes).append(soulInput.readString()).append("\n").toString()
            i++
        }
        val nDislikes = soulInput.readInt()
        var dislikes = String()
        i = 0
        while (i < nDislikes) {
            dislikes = StringBuilder(dislikes).append(soulInput.readString()).append("\n").toString()
            i++
        }
        val activity: Activity = Util.uiActivity
        if (activity.getClass() === ProfileActivity::class.java && (activity as ProfileActivity).peerName.equals(user)) {
            (activity as ProfileActivity).updateLikes(likes, dislikes)
        }*/
    }


    private fun receiveGlobalAdminMessage() {
        //Util.toast(this, "Admin Message: " + soulInput.readString())
    }


    private fun receivePrivilegedUsers() {
        /*val nUsers = soulInput.readInt()
        this.service.privilegedUsers.clear()
        for (i in 0 until nUsers) {
            this.service.privilegedUsers.add(soulInput.readString())
        }
        println( "privileged users loaded")*/
    }


    private fun receiveAddPrivilegedUser() {
        //this.service.privilegedUsers.add(soulInput.readString())
    }


    private fun receiveCheckPrivileges() {
        soulInput.readInt()
    }


    private fun receiveSearchRequest() {
        val distributedCode: Byte = soulInput.readByte()
        val unknown = soulInput.readInt()
        val username = soulInput.readString()
        val token = soulInput.readInt()
        val query = soulInput.readString()
    }


    private fun receiveNetInfo() {
        val nParents = soulInput.readInt()
        val parentUser = arrayOfNulls<String>(nParents)
        val parentIp = arrayOfNulls<String>(nParents)
        val parentPort = IntArray(nParents)
        for (i in 0 until nParents) {
            parentUser[i] = soulInput.readString()
            parentIp[i] = soulInput.readIp()
            parentPort[i] = soulInput.readInt()
        }
    }


    private fun receiveWishlistInterval() {
        val interval = soulInput.readInt()
    }


    private fun receiveGetSimilarUsers() {
        val nUsers = soulInput.readInt()
        val user = arrayOfNulls<String>(nUsers)
        val status = IntArray(nUsers)
        for (i in 0 until nUsers) {
            user[i] = soulInput.readString()
            status[i] = soulInput.readInt()
        }
    }


    private fun receiveGetItemRecommendations() {
        val item = soulInput.readString()
        val nRecs = soulInput.readInt()
        val recs = arrayOfNulls<String>(nRecs)
        val receivedValues = IntArray(nRecs)
        for (i in 0 until nRecs) {
            recs[i] = soulInput.readString()
            receivedValues[i] = soulInput.readInt()
        }
    }


    private fun receiveGetItemSimilarUsers() {
        val item = soulInput.readString()
        val nUsers = soulInput.readInt()
        val user = arrayOfNulls<String>(nUsers)
        for (i in 0 until nUsers) {
            user[i] = soulInput.readString()
            soulInput.readInt()
        }
    }


    private fun receiveRoomTickers() {
        /*var i: Int
        val room = soulInput.readString()
        val nUsers = soulInput.readInt()
        val user = arrayOfNulls<String>(nUsers)
        val ticker = arrayOfNulls<String>(nUsers)
        i = 0
        while (i < nUsers) {
            user[i] = soulInput.readString()
            ticker[i] = soulInput.readString()
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
        //GoSeekData.newTicker(soulInput.readString(), soulInput.readString(), soulInput.readString())
    }


    private fun receiveRoomTickerRemove() {
        //GoSeekData.removeTicker(soulInput.readString(), soulInput.readString())
    }


    private fun receiveUserPrivileges() {
        /* val user = soulInput.readString()
         if (soulInput.readBoolean()) {
             if (!this.service.privilegedUsers.contains(user)) {
                 this.service.privilegedUsers.add(user)
             }
         } else if (this.service.privilegedUsers.contains(user)) {
             this.service.privilegedUsers.remove(user)
         }*/
    }


    private fun receiveAcknowledgeNotifyPrivileges() {
        val token = soulInput.readInt()
    }


    private fun receivePrivateRoomUsers() {
        val room = soulInput.readString()
        val nUsers = soulInput.readInt()
        val users = arrayOfNulls<String>(nUsers)
        for (i in 0 until nUsers) {
            users[i] = soulInput.readString()
        }
    }

    private fun receivePrivateRoomAddUser() {
        val room = soulInput.readString()
        val user = soulInput.readString()
    }

    private fun receivePrivateRoomRemoveUser() {
        val room = soulInput.readString()
        val user = soulInput.readString()
    }


    private fun receivePrivateRoomAdded() {
        val room = soulInput.readString()
    }


    private fun receivePrivateRoomRemoved() {
        val room = soulInput.readString()
    }


    private fun receivePrivateRoomToggle() {
        val inviteEnabled = soulInput.readBoolean()
    }


    private fun receiveNewPassword() {
        /*val password = soulInput.readString()
        Util.toast(this, "Password Successfully Changed.")*/
    }


    private fun receivePrivateRoomAddOperator() {
        val room = soulInput.readString()
        val operator = soulInput.readString()
    }


    private fun receivePrivateRoomRemoveOperator() {
        val room = soulInput.readString()
        val operator = soulInput.readString()
    }


    private fun receivePrivateRoomOperatorAdded() {
        val room = soulInput.readString()
    }


    private fun receivePrivateRoomOperatorRemoved() {
        val room = soulInput.readString()
    }


    private fun receivePrivateRoomOwned() {
        val room = soulInput.readString()
        val nOperators = soulInput.readInt()
        val operator = arrayOfNulls<String>(nOperators)
        for (i in 0 until nOperators) {
            operator[i] = soulInput.readString()
        }
    }


    private fun receivePublicChat() {
        val room = soulInput.readString()
        val user = soulInput.readString()
        val message = soulInput.readString()
    }


    private fun receiveCannotConnect() {
        val token = soulInput.readInt()
        //onReceiveCannotConnect(token)
    }

    /* SENT TO SERVER */
    private fun login(login: String, pwd: String) {
        sendMessage(
            ByteMessage().writeInt32(1)
                .writeStr(login)
                .writeStr(pwd)
                .writeInt32(160)
                .writeStr(Hash.toMd5(login + pwd))
                .writeInt32(1)
                .getBuff()
        )
    }

    private fun setListenPort(port: Int) {
        sendMessage(
            ByteMessage()
                .writeInt32(2)
                .getBuff()
        )
    }

    fun getPeerAddressByUsername(username: String) {
        sendMessage(
            ByteMessage()
                .writeInt32(3)
                .writeStr(username)
                .getBuff()
        )
    }

    private fun addUser(username: String) {
        sendMessage(
            ByteMessage()
                .writeInt32(5)
                .writeStr(username)
                .getBuff()
        )
    }

    fun joinRoom(roomName: String /*Room room*/) {
        sendMessage(
            ByteMessage()
                .writeInt32(14)
                .writeStr(roomName /*room.getName()*/)
                .getBuff()
        )
    }

    private fun setStatus(status: Int) {
        sendMessage(
            ByteMessage()
                .writeInt32(28)
                .writeInt32(status)
                .getBuff()
        )
    }

    private fun sharedFoldersFiles(folderCount: Int, fileCount: Int) {
        sendMessage(
            ByteMessage()
                .writeInt32(26)
                .writeInt32(folderCount)
                .writeInt32(fileCount)
                .getBuff()
        )
    }

    private fun haveNoParents(flag: Int) {
        sendMessage(
            ByteMessage()
                .writeInt32(71)
                .writeInt32(flag)
                .getBuff()
        )
    }

    private fun parentIp(ip: IntArray) {
        sendMessage(
            ByteMessage()
                .writeInt32(73)
                .writeInt8(ip[0])
                .writeInt8(ip[1])
                .writeInt8(ip[2])
                .writeInt8(ip[3])
                .getBuff()
        )
    }

    fun fileSearch(query: String) {
        val token = Random.nextInt(Integer.MAX_VALUE)
        SoulStack.searches[token] = query
        SoulStack.actualSearchToken = token
        println("search: ${SoulStack.searches[SoulStack.actualSearchToken]}, token: ${SoulStack.actualSearchToken}")

        sendMessage(
            ByteMessage()
                .writeInt32(26)
                .writeInt32(token)
                .writeStr(query)
                .getBuff()
        )
    }

    fun userSearch(username: String, ticket: Int, query: String) {
        val token = Random.nextInt(Integer.MAX_VALUE)
        SoulStack.searches[token] = query
        SoulStack.actualSearchToken = token
        sendMessage(
            ByteMessage()
                .writeInt32(42)
                .writeStr(username)
                .writeInt32(ticket)
                .writeStr(query)
                .getBuff()
        )
    }

    fun sendRoomMessage(roomMessageApiModel: RoomMessageApiModel) {
        sendMessage(
            ByteMessage()
                .writeInt32(13)
                .writeStr(roomMessageApiModel.room)
                .writeStr(roomMessageApiModel.message)
                .getBuff()
        )
    }

    private suspend fun receiveRoomList() {
        val rooms = arrayListOf<RoomApiModel>()

        val publicRooms = arrayListOf<RoomApiModel>()
        val nbPublicRooms = soulInput.readInt()
        for (j in 0 until nbPublicRooms) {
            publicRooms.add(
                RoomApiModel(
                    name = soulInput.readString()
                )
            )
        }
        for (room in publicRooms) {
            room.nbUsers = soulInput.readInt()
        }
        rooms.addAll(publicRooms)
/*
        val ownedRooms = arrayListOf<RoomApiModel>()
        val nbOwnedRooms = soulInput.readInt()
        for (j in 0 until nbOwnedRooms) {
            val roomName = soulInput.readString()
            ownedRooms.add(
                RoomApiModel(
                    name = roomName,
                    owner = true,
                    private = true,
                    operated = false
                )
            )
        }
        for (room in ownedRooms) {
            val nbUsers = soulInput.readInt()
            room.nbUsers = nbUsers
        }
        rooms.addAll(ownedRooms)

        val privateRooms = arrayListOf<RoomApiModel>()
        val nbPrivateRooms = soulInput.readInt()
        for (j in 0 until nbPrivateRooms) {
            val roomName = soulInput.readString()
            privateRooms.add(
                RoomApiModel(
                    name = roomName,
                    owner = false,
                    private = true,
                    operated = false
                )
            )
        }
        for (room in privateRooms) {
            val nbUsers = soulInput.readInt()
            room.nbUsers = nbUsers
        }
        rooms.addAll(privateRooms)

        val operatedRooms = arrayListOf<RoomApiModel>()
        val nbOperatedRooms = soulInput.readInt()
        for (j in 0 until nbOperatedRooms) {
            val roomName = soulInput.readString()
            operatedRooms.add(
                RoomApiModel(
                    name = roomName,
                    owner = false,
                    private = false,
                    operated = true
                )
            )
        }
        for (room in operatedRooms) {
            val nbUsers = soulInput.readInt()
            room.nbUsers = nbUsers
        }
        rooms.addAll(operatedRooms)
*/
        RoomRepository.setRooms(rooms)
    }
}