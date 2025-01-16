package fr.sercurio.soulseek.client.server

import fr.sercurio.soulseek.client.shared.ResponseCallback
import fr.sercurio.soulseek.client.shared.AbstractSocket
import fr.sercurio.soulseek.client.server.messages.ConnectToPeerMessage
import fr.sercurio.soulseek.client.server.messages.LoginMessage
import fr.sercurio.soulseek.client.server.messages.RoomListMessage
import fr.sercurio.soulseek.client.server.messages.SayInRoomMessage
import fr.sercurio.soulseek.client.shared.model.ByteMessage
import fr.sercurio.soulseek.server.entities.RoomApiModel
import fr.sercurio.soulseek.server.model.LoginResponse
import fr.sercurio.soulseek.server.toMD5
import fr.sercurio.soulseek.server.utils.SoulStack
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeoutException
import kotlin.random.Random

class ServerSocket(
    host: String,
    port: Int,
    private val loginCallback: ResponseCallback<LoginMessage> = ResponseCallback(),
    private val roomListCallback: ResponseCallback<RoomListMessage> = ResponseCallback(),
    private val sayInRoomCallback: ResponseCallback<SayInRoomMessage> = ResponseCallback(),
    private val connectToPeerCallback: ResponseCallback<ConnectToPeerMessage> = ResponseCallback(),
) : AbstractSocket(host, port) {
    private val loginDeferred = CompletableDeferred<LoginResponse>()


    override suspend fun onSocketConnected() {
        //        login(login, password)
        //        setListenPort(listenPort)
    }

    override suspend fun whileConnected() {
        try {
            readChannel.readAndSetMessageLength()
            val code = readChannel.readInt()

            println(
                "ServerClient received: Message code: $code Packet Size: ${readChannel.packLeft + 4}"
            )

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
            readChannel.skipPackLeft()
        } catch (e: Exception) {
            throw e
        }
    }

    fun onLogin(callback: (LoginMessage) -> Unit) {
        loginCallback.subscribe(callback)
    }

    private suspend fun receiveLogin() {
        if (readChannel.readBoolean()) {
            val greeting = readChannel.readString()
            val ip = readChannel.readInt()

            loginCallback.update(LoginMessage(true, greeting, ip, null))
        } else {
            val reason: String = readChannel.readString()
            loginCallback.update(LoginMessage(false, null, null, reason))
        }
    }

    private suspend fun receiveLoginTest() {
        if (readChannel.readBoolean()) {
            val greeting = readChannel.readString()
            val ip = readChannel.readInt()

            loginDeferred.complete(LoginResponse(true, greeting, ip, null))
//            loginCallback.update(LoginMessage(true, greeting, ip, null))
        } else {
            val reason: String = readChannel.readString()
            loginDeferred.complete(LoginResponse(false, null, null, reason))
//            loginCallback.update(LoginMessage(false, null, null, reason))
        }
    }

    private suspend fun receiveGetPeerAddress() {
        val username = readChannel.readString()
        val ip: String = readChannel.readIp()
        val port = readChannel.readInt()

        // listener.onGetPeerAddress(username = username, host = ip, port = port)
    }

    private suspend fun receiveAddUser() {
        val user = readChannel.readString()
        if (readChannel.readBoolean()) {
            val status = readChannel.readInt()
            val avgSpeed = readChannel.readInt()
            val downloadNum: Long = readChannel.readLong()
            val files = readChannel.readInt()
            val dirs = readChannel.readInt()
            readChannel.readString()
        }
    }

    private suspend fun receiveGetStatus() {
        val username = readChannel.readString()
        val status = readChannel.readInt()
        val privileged = readChannel.readBoolean()
    }

    fun onSayInChatRoom(callback: (SayInRoomMessage) -> Unit) {
        sayInRoomCallback.subscribe(callback)
    }

    suspend fun receiveSayInChatRoom() {
        val room = readChannel.readString()
        val username = readChannel.readString()
        val message = readChannel.readString()
        sayInRoomCallback.update(SayInRoomMessage(room, username, message))
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
        val room = readChannel.readString()
        sayInRoomCallback.update(SayInRoomMessage(room, "SERVER", "Leaving room"))
    }

    private suspend fun receiveUserJoinedRoom() {
        val room = readChannel.readString()
        val username = readChannel.readString()
        val status = readChannel.readInt()
        val avgspeed = readChannel.readInt()
        val downloadNum: Long = readChannel.readLong()
        val files = readChannel.readInt()
        val dirs = readChannel.readInt()
        val slotsFree = readChannel.readInt()
        val countryCode = readChannel.readString()

        // listener.onUserJoinedRoom(
        //   room, username, status, avgspeed, downloadNum, files, dirs, slotsFree, countryCode)
    }

    private suspend fun receiveUserLeftRoom() {
        val roomName = readChannel.readString()
        val username = readChannel.readString()

        // listener.onUserLeftRoom(roomName, username)
    }

    fun onReceiveConnectToPeer(callback: (ConnectToPeerMessage) -> Unit) {
        connectToPeerCallback.subscribe { callback(it) }
    }

    private suspend fun receiveConnectToPeer() {
        val username = readChannel.readString()
        val type = readChannel.readString()
        val ip: String = readChannel.readIp()
        val port = readChannel.readInt()
        val token = readChannel.readInt()
        val obfuscatedPort = readChannel.readBoolean()

        connectToPeerCallback.update(
            ConnectToPeerMessage(username, type, ip, port, token, obfuscatedPort)
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
        // listener.onKickedFromServer()
        // Util.toast(this, "You were kicked from the server.")
    }

    private suspend fun receiveGetRecommendations() {
        val nRecs = readChannel.readInt()
        val recs = arrayOfNulls<String>(nRecs)
        val recLevel = IntArray(nRecs)
        var i: Int = 0
        while (i < nRecs) {
            recs[i] = readChannel.readString()
            recLevel[i] = readChannel.readInt()
            i++
        }
        val nUnRecs = readChannel.readInt()
        val unRecs = arrayOfNulls<String>(nUnRecs)
        val unRecLevel = IntArray(nUnRecs)
        i = 0
        while (i < nUnRecs) {
            unRecs[i] = readChannel.readString()
            unRecLevel[i] = readChannel.readInt()
            i++
        }
    }

    private suspend fun receiveGetGlobalRecommendations() {
        val nRecs = readChannel.readInt()
        val recs = arrayOfNulls<String>(nRecs)
        val recLevel = IntArray(nRecs)
        var i: Int = 0
        while (i < nRecs) {
            recs[i] = readChannel.readString()
            recLevel[i] = readChannel.readInt()
            i++
        }
        val nUnRecs = readChannel.readInt()
        val unRecs = arrayOfNulls<String>(nUnRecs)
        val unRecLevel = IntArray(nUnRecs)
        i = 0
        while (i < nUnRecs) {
            unRecs[i] = readChannel.readString()
            unRecLevel[i] = readChannel.readInt()
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
        // Util.toast(this, "Admin Message: " + soulInput.readString())
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
        // this.service.privilegedUsers.add(soulInput.readString())
    }

    private suspend fun receiveCheckPrivileges() {
        readChannel.readInt()
    }

    private suspend fun receiveSearchRequest() {
        val distributedCode: Byte = readChannel.readByte()
        val unknown = readChannel.readInt()
        val username = readChannel.readString()
        val token = readChannel.readInt()
        val query = readChannel.readString()
    }

    private suspend fun receiveNetInfo() {
        val nParents = readChannel.readInt()
        val parentUser = arrayOfNulls<String>(nParents)
        val parentIp = arrayOfNulls<String>(nParents)
        val parentPort = IntArray(nParents)
        for (i in 0 until nParents) {
            parentUser[i] = readChannel.readString()
            parentIp[i] = readChannel.readIp()
            parentPort[i] = readChannel.readInt()
        }
    }

    private suspend fun receiveWishlistInterval() {
        val interval = readChannel.readInt()
    }

    private suspend fun receiveGetSimilarUsers() {
        val nUsers = readChannel.readInt()
        val user = arrayOfNulls<String>(nUsers)
        val status = IntArray(nUsers)
        for (i in 0 until nUsers) {
            user[i] = readChannel.readString()
            status[i] = readChannel.readInt()
        }
    }

    private suspend fun receiveGetItemRecommendations() {
        val item = readChannel.readString()
        val nRecs = readChannel.readInt()
        val recs = arrayOfNulls<String>(nRecs)
        val receivedValues = IntArray(nRecs)
        for (i in 0 until nRecs) {
            recs[i] = readChannel.readString()
            receivedValues[i] = readChannel.readInt()
        }
    }

    private suspend fun receiveGetItemSimilarUsers() {
        val item = readChannel.readString()
        val nUsers = readChannel.readInt()
        val user = arrayOfNulls<String>(nUsers)
        for (i in 0 until nUsers) {
            user[i] = readChannel.readString()
            readChannel.readInt()
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
        // GoSeekData.newTicker(soulInput.readString(), soulInput.readString(),
        // soulInput.readString())
    }

    private fun receiveRoomTickerRemove() {
        // GoSeekData.removeTicker(soulInput.readString(), soulInput.readString())
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

    private suspend fun receiveAcknowledgeNotifyPrivileges() {
        val token = readChannel.readInt()
    }

    private suspend fun receivePrivateRoomUsers() {
        val room = readChannel.readString()
        val nUsers = readChannel.readInt()
        val users = arrayOfNulls<String>(nUsers)
        for (i in 0 until nUsers) {
            users[i] = readChannel.readString()
        }
    }

    private suspend fun receivePrivateRoomAddUser() {
        val room = readChannel.readString()
        val user = readChannel.readString()
    }

    private suspend fun receivePrivateRoomRemoveUser() {
        val room = readChannel.readString()
        val user = readChannel.readString()
    }

    private suspend fun receivePrivateRoomAdded() {
        val room = readChannel.readString()
    }

    private suspend fun receivePrivateRoomRemoved() {
        val room = readChannel.readString()
    }

    private suspend fun receivePrivateRoomToggle() {
        val inviteEnabled = readChannel.readBoolean()
    }

    private fun receiveNewPassword() {
        /*val password = soulInput.readString()
        Util.toast(this, "Password Successfully Changed.")*/
    }

    private suspend fun receivePrivateRoomAddOperator() {
        val room = readChannel.readString()
        val operator = readChannel.readString()
    }

    private suspend fun receivePrivateRoomRemoveOperator() {
        val room = readChannel.readString()
        val operator = readChannel.readString()
    }

    private suspend fun receivePrivateRoomOperatorAdded() {
        val room = readChannel.readString()
    }

    private suspend fun receivePrivateRoomOperatorRemoved() {
        val room = readChannel.readString()
    }

    private suspend fun receivePrivateRoomOwned() {
        val room = readChannel.readString()
        val nOperators = readChannel.readInt()
        val operator = arrayOfNulls<String>(nOperators)
        for (i in 0 until nOperators) {
            operator[i] = readChannel.readString()
        }
    }

    private suspend fun receivePublicChat() {
        val room = readChannel.readString()
        val user = readChannel.readString()
        val message = readChannel.readString()
    }

    private suspend fun receiveCannotConnect() {
        val token = readChannel.readInt()
        // onReceiveCannotConnect(token)
    }

    /* SENT TO SERVER */
    suspend fun login(login: String, pwd: String) {
        send(
            ByteMessage()
                .writeInt32(1)
                .writeStr(login)
                .writeStr(pwd)
                .writeInt32(160)
                .writeStr((login + pwd).toMD5())
                .writeInt32(1)
                .getBuff()
        )
    }

    suspend fun loginTest(login: String, pwd: String): LoginResponse {
        send(
            ByteMessage()
                .writeInt32(1)
                .writeStr(login)
                .writeStr(pwd)
                .writeInt32(160)
                .writeStr((login + pwd).toMD5())
                .writeInt32(1)
                .getBuff()
        )

        return try {
            withTimeout(5000) {
                loginDeferred.await()
            }
        } catch (_: TimeoutCancellationException) {
            throw TimeoutException("Login response timed out")
        }
    }

    private suspend fun setListenPort(port: Int) {
        send(ByteMessage().writeInt32(2).getBuff())
    }

    suspend fun getPeerAddressByUsername(username: String) {
        send(ByteMessage().writeInt32(3).writeStr(username).getBuff())
    }

    private suspend fun addUser(username: String) {
        send(ByteMessage().writeInt32(5).writeStr(username).getBuff())
    }

    suspend fun joinRoom(roomName: String /*Room room*/) {
        send(ByteMessage().writeInt32(14).writeStr(roomName /*room.getName()*/).getBuff())
    }

    private suspend fun setStatus(status: Int) {
        send(ByteMessage().writeInt32(28).writeInt32(status).getBuff())
    }

    private suspend fun sharedFoldersFiles(folderCount: Int, fileCount: Int) {
        send(ByteMessage().writeInt32(26).writeInt32(folderCount).writeInt32(fileCount).getBuff())
    }

    private suspend fun haveNoParents(flag: Int) {
        send(ByteMessage().writeInt32(71).writeInt32(flag).getBuff())
    }

    private suspend fun parentIp(ip: IntArray) {
        send(
            ByteMessage()
                .writeInt32(73)
                .writeInt8(ip[0])
                .writeInt8(ip[1])
                .writeInt8(ip[2])
                .writeInt8(ip[3])
                .getBuff()
        )
    }

    suspend fun fileSearch(query: String, token: Int) {
        send(ByteMessage().writeInt32(26).writeInt32(token).writeStr(query).getBuff())
    }

    suspend fun userSearch(username: String, ticket: Int, query: String) {
        val token = Random.nextInt(Integer.MAX_VALUE)
        SoulStack.searches[token] = query
        SoulStack.actualSearchToken = token
        send(
            ByteMessage()
                .writeInt32(42)
                .writeStr(username)
                .writeInt32(ticket)
                .writeStr(query)
                .getBuff()
        )
    }

    suspend fun sendRoomMessage(room: String, message: String) {
        send(ByteMessage().writeInt32(13).writeStr(room).writeStr(message).getBuff())
    }

    fun onReceiveRoomList(callback: (RoomListMessage) -> Unit) {
        roomListCallback.subscribe { callback(it) }
    }

    private suspend fun receiveRoomList() {
        val rooms = arrayListOf<RoomApiModel>()

        val publicRooms = arrayListOf<RoomApiModel>()
        val nbPublicRooms = readChannel.readInt()
        for (j in 0 until nbPublicRooms) {
            publicRooms.add(RoomApiModel(name = readChannel.readString()))
        }
        for (room in publicRooms) {
            room.nbUsers = readChannel.readInt()
        }
        rooms.addAll(publicRooms) /*
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
        roomListCallback.update(RoomListMessage(rooms))
    }
}
