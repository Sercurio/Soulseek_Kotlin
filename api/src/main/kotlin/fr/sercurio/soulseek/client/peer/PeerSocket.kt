package fr.sercurio.soulseek.client.peer

import fr.sercurio.soulseek.ResponseCallback
import fr.sercurio.soulseek.SoulInputStream
import fr.sercurio.soulseek.client.AbstractSocket
import fr.sercurio.soulseek.client.peer.messages.SearchReplyMessage
import fr.sercurio.soulseek.client.peer.messages.TransferRequestMessage
import fr.sercurio.soulseek.entities.ByteMessage
import fr.sercurio.soulseek.entities.SoulFile
import fr.sercurio.soulseek.toInt
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.Inflater

class PeerSocket(
    host: String,
    port: Int,

    private val token: Int,
    val username: String,

    private val searchReplyCallback: ResponseCallback<SearchReplyMessage> = ResponseCallback(),
    private val transferRequestCallback: ResponseCallback<TransferRequestMessage> =
        ResponseCallback()
) : AbstractSocket(host, port) {
    override suspend fun onSocketConnected() {
        println("Connected to $username")
        pierceFirewall(token)
    }

    override suspend fun whileConnected() {
        try {
            if(readChannel.byteReadChannel.isClosedForRead.not()) {

                readChannel.readAndSetMessageLength()
                val code = readChannel.readInt()
                println("PeerClient received: Message code: $code Packet Size: ${readChannel.packLeft + 4}")

                when (code) {
                    4 -> receiveSharesRequest()
                    5 -> receiveSharesReply()
                    8 -> receiveSearchRequest()
                    9 -> fileSearchResponse()
                    15 -> receiveInfoRequest()
                    16 -> receiveInfoReply()
                    36 -> receiveFolderContentsRequest()
                    37 -> receiveFolderContentsReply()
                    40 -> receiveTransferRequest()
                    41 -> receiveTransferReply()
                    43 -> receiveQueueDownload()
                    44 -> receivePlaceInQueueReply()
                    46 -> receiveUploadFailed()
                    50 -> receiveQueueFailed()
                    51 -> receivePlaceInQueueRequest()
                    52 -> receiveUploadQueueNotification()
                }
                readChannel.skipPackLeft()
            }
        } catch (e: Exception) {
            throw e
        }
        delay(500)
    }

    private fun receiveSharesRequest() {
        println("received share request")
        // sendSharesReply()
    }

    private suspend fun receiveSharesReply() {
        val messageDeflated = ByteArray(readChannel.packLeft)
        readChannel.byteReadChannel.readFully(messageDeflated, 0, readChannel.packLeft)

        val inflater = Inflater()
        inflater.setInput(messageDeflated)

        val buffer = ByteArray(1024)
        val outputStream = ByteArrayOutputStream()

        while (!inflater.finished()) {
            val count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }
        val inflatedReadChannel = SoulInputStream(ByteReadChannel(outputStream.toByteArray()))

        println("Loading $username shares.")
        val nDirs = inflatedReadChannel.readInt()
        // val hashMap: HashMap<String?, ShareDirectory?> = HashMap<Any?, Any?>(nDirs)
        println("Loading $nDirs folders.")
        for (i in 0 until nDirs) {
            val dirName = inflatedReadChannel.readString()
            /*val parent: ShareDirectory? = hashMap[Util.getFolderPath(dirName)] as ShareDirectory?
            val currentDir = ShareDirectory(dirName, parent)
            hashMap[dirName] = currentDir
            if (parent == null) {
                this.service.rootShare.put(this.peerName, currentDir)
            }*/
            val nFiles = inflatedReadChannel.readInt()
            for (j in 0 until nFiles) {
                inflatedReadChannel.readByte()
                val filename = inflatedReadChannel.readString()
                val fileSize = inflatedReadChannel.readLong()
                inflatedReadChannel.readString()
                val nAttributes = inflatedReadChannel.readInt()
                var bitrate = 0
                var length = 0
                var vbr = 0
                for (k in 0 until nAttributes) {
                    when (inflatedReadChannel.readInt()) {
                        0 -> bitrate = inflatedReadChannel.readInt()
                        1 -> length = inflatedReadChannel.readInt()
                        2 -> vbr = inflatedReadChannel.readInt()
                        else -> inflatedReadChannel.readInt()
                    }
                }
                // val shareFile = ShareFile(currentDir, filename, filesize, bitrate, length, vbr)
                println("decodeFiles: $dirName, $filename")
            }
        }
        println("Finished Loading $username shares.")
    }

    private suspend fun receiveSearchRequest() {
        val ticket = readChannel.readInt()
        val query = readChannel.readString() /*val cursor: Cursor = GoSeekData.searchShares(query)
        if (cursor != null) {
            sendSearchReply(ticket, query, cursor)
        }*/
    }

    fun onReceiveSearchReply(callback: (SearchReplyMessage) -> Unit) {
        searchReplyCallback.subscribe { callback(it) }
    }

    private suspend fun fileSearchResponse() {
        val messageDeflated = ByteArray(readChannel.packLeft)
        readChannel.byteReadChannel.readFully(messageDeflated, 0, readChannel.packLeft)

        val inflater = Inflater()
        inflater.setInput(messageDeflated)

        val buffer = ByteArray(1024)
        val outputStream = ByteArrayOutputStream()

        while (!inflater.finished()) {
            val count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }
        val inflatedReadChannel = SoulInputStream(ByteReadChannel(outputStream.toByteArray()))

        val soulFiles = arrayListOf<SoulFile>()

        val user = inflatedReadChannel.readString()
        val token = inflatedReadChannel.readInt()
        var path = ""
        var size: Long
        var extension = ""
        var bitrate = 0
        var duration = 0
        var vbr = 0
        val slotsFree: Boolean
        val avgSpeed: Int
        val queueLength: Long
        if (true /*TODO search the ticket*/) {
            val nResults = inflatedReadChannel.readInt()
            for (i in 0 until nResults) {
                inflatedReadChannel.readBoolean() // unused
                path = inflatedReadChannel.readString().replace("\\", "/")
                size = inflatedReadChannel.readLong()
                extension = inflatedReadChannel.readString()
                val nAttr = inflatedReadChannel.readInt()
                for (j in 0 until nAttr) {
                    when (val posAttr = inflatedReadChannel.readInt()) {
                        0 -> bitrate = inflatedReadChannel.readInt()
                        1 -> duration = inflatedReadChannel.readInt()
                        2 -> vbr = inflatedReadChannel.readInt()
                        else -> inflatedReadChannel.readInt()
                    }
                }
                var filename = ""
                var folder = ""
                var folderPath = ""
                val a = path.lastIndexOf("/")
                if (a > 0 && a < path.length) {
                    filename = path.substring(a + 1)
                    folderPath = path.substring(0, a)
                    val s = folderPath.lastIndexOf("/")
                    folder = if (s < 0) "/" else folderPath.substring(s)
                }
                soulFiles.add(
                    SoulFile(
                        path,
                        filename,
                        folderPath,
                        folder,
                        size,
                        extension,
                        bitrate,
                        vbr,
                        duration
                    )
                )
            }
            slotsFree = inflatedReadChannel.readBoolean()
            avgSpeed = inflatedReadChannel.readInt()
            queueLength = inflatedReadChannel.readLong()

            println("Received $nResults search results from $username}")

            readChannel.packLeft = 0
            if (soulFiles.isNotEmpty()) {
                searchReplyCallback.update(
                    SearchReplyMessage(user, token, soulFiles, slotsFree, avgSpeed, queueLength)
                )
            }
        }
    }

    private fun receiveInfoRequest() {}

    private suspend fun receiveInfoReply() {
        val description = readChannel.readString()
        if (readChannel.readBoolean()) {
            val picture = readChannel.readString()
        }
        val totalupl = readChannel.readInt()
        val queuesize = readChannel.readInt()
        val slotsfree = readChannel.readInt()
        println("Received User Info Reply.") /*val activity: Activity = Util.uiActivity
        if (activity.getClass() === ProfileActivity::class.java && (activity as ProfileActivity).peerName.equals(this.peerName)) {
            (activity as ProfileActivity).updateProfile(description)
        }*/
    }

    private suspend fun receiveFolderContentsRequest() {
        val nFiles = readChannel.readInt()
        val file = arrayOfNulls<String>(nFiles)
        for (i in 0 until nFiles) {
            file[i] = readChannel.readString()
        }
        println("Received Folder Contents Request.")
        // sendFolderContentsReply(file)
    }

    private fun receiveFolderContentsReply() {
        /*var i: Int
        val dataInputStream = DataInputStream(InflaterInputStream(dis))
        var nFolders = soulInput.readInt(dataInputStream)
        i = 0
        while (i < nFolders) {
            soulInput.readString(dataInputStream)
            i++
        }
        nFolders = soulInput.readInt(dataInputStream)
        i = 0
        while (i < nFolders) {
            val dir = soulInput.readString(dataInputStream)
            val nFiles = soulInput.readInt(dataInputStream)
            println( "Parsing " + dir + " from:" + this.peerName + ".")
            var currentDir: ShareDirectory? = this.service.rootShare.get(this.peerName) as ShareDirectory
            if (currentDir == null) {
                currentDir = ShareDirectory(dir, null)
                this.service.rootShare.put(this.peerName, currentDir)
            } else {
                currentDir = currentDir.open(dir)
            }
            for (j in 0 until nFiles) {
                soulInput.readByte(dataInputStream)
                val file = soulInput.readString(dataInputStream)
                val size = soulInput.readLong(dataInputStream)
                val ext = soulInput.readString(dataInputStream)
                val nAttrs = soulInput.readInt(dataInputStream)
                var bitrate = 0
                var length = 0
                var vbr = 0
                for (k in 0 until nAttrs) {
                    when (soulInput.readInt(dataInputStream)) {
                        0 -> bitrate = soulInput.readInt(dataInputStream)
                        1 -> length = soulInput.readInt(dataInputStream)
                        2 -> vbr = soulInput.readInt(dataInputStream)
                        else -> soulInput.readInt(dataInputStream)
                    }
                }
                val shareFile = ShareFile(currentDir, file, size, bitrate, length, vbr)
            }
            i++
        }
        println( "Finished Loading " + this.peerName.toString() + " folder reply.")
        val a: Activity = Util.uiActivity
        if (a != null && a.getClass() === BrowsePeerActivity::class.java) {
            (a as BrowsePeerActivity).update(this.peerName)
        }*/
    }

    fun onTransferRequest(callback: (TransferRequestMessage) -> Unit) {
        transferRequestCallback.subscribe { callback(it) }
    }

    private suspend fun receiveTransferRequest() {
        val direction = readChannel.readInt()
        val token = readChannel.readInt()
        val path = readChannel.readString()
        val size: Long
        if (direction == 1) {
            size = readChannel.readLong()
            println("Peer:  $username wants to send us a file: $path")

            transferRequestCallback.update(TransferRequestMessage(direction, token, path, size))
        } else println("Peer: $username wants to download a file: $path")
    }

    private suspend fun receiveTransferReply() {
        println("Received transfer reply.")
        val ticket = readChannel.readInt()
        val allowed = readChannel.readBoolean()
        if (allowed && readChannel.packLeft >= 8) {
            println("Allowed!")
            val filesize = readChannel.readLong()
        } else if (!allowed) {
            println(
                "Not Allowed:" +
                        readChannel.readString()
            ) /*val goSeekService: GoSeekService = this.service
                goSeekService.pendingUploads--*/
        }
    }

    private suspend fun receiveQueueDownload() {
        val filename = readChannel.readString() /* println( "Received a queue download request.")
         this.service.queueUpload(this.peerName, filename)*/
    }

    private fun receivePlaceInQueueReply() {
        /* GoSeekData.updateDownloadPlace(this.peerName, soulInput.readString(), soulInput.readInt())
        val a: Activity = Util.uiActivity
        if (a.getClass() === TransfersActivity::class.java) {
            (a as TransfersActivity).update()
        }*/
    }

    private suspend fun receiveUploadFailed() {
        val reason = readChannel.readString()
        println(reason)
    }

    private suspend fun receiveQueueFailed() {
        val path = readChannel.readString()
        println("Queue Failed. Reason: " + readChannel.readString())
    }

    private fun receivePlaceInQueueRequest() {
        /*val filename = soulInput.readString()
        val c: Cursor = GoSeekData.getUpload(this.peerName, filename)
        if (c != null && c.getCount() > 0) {
            c.moveToFirst()
            sendPlaceInQueueReply(filename, c.getInt(c.getColumnIndex("place")))
        }*/
    }

    private fun receiveUploadQueueNotification() {}

    private fun deflate(data: ByteArray): ByteArray? {
        val inflater = Inflater()
        inflater.setInput(data)
        val outputStream = ByteArrayOutputStream(data.size)
        val buffer = ByteArray(1024)
        while (!inflater.finished()) {
            val count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }
        outputStream.close()
        return outputStream.toByteArray()
    }

    suspend fun pierceFirewall(token: Int) {
        send(ByteMessage().writeInt8(0).writeInt32(token).getBuff())
    }

    suspend fun peerInit(username: String, connectionType: String, token: Int) {
        send(
            ByteMessage()
                .writeInt8(1)
                .writeStr(username)
                .writeStr(connectionType) // .writeInt(300)
                .writeInt32(token)
                .getBuff()
        )
    }

    private suspend fun getShareFileList() {
        send(ByteMessage().writeInt8(4).getBuff())
    }

    suspend fun fileSearchRequest(token: Int, query: String) {
        send(ByteMessage().writeInt8(8).writeInt32(token).writeStr(query).getBuff())
    }

    private suspend fun userInfoRequest() {
        send(ByteMessage().writeInt8(15).getBuff())
    }

    /*getShareFileList: () => new Message().int32(4),
    sharedFileList: shareList => {
        let msg = new Message().int32(5);
        encodeList.shares(msg, shareList);
        return msg;
    },
    fileSearchResult: args => {
        let msg = new Message().str(args.username).int32(args.token);
        encodeList.files(msg, args.fileList);
        msg.int8(args.slotsFree).int32(args.speed).int64(args.queueSize);
        return new Message().int32(9).writegetBuff()er(zlib.deflateSync(msg.data));
    },

    userInfoReply: args => {
        let msg = new Message().int32(16).str(args.description);

        if (args.picture) {
            msg.int8(true).file(args.picture);
        } else {
            msg.int8(false);
        }

        msg.int32(args.uploadSlots).int32(args.queueSize).int8(args.slotsFree);
        // who we accept uploads from
        msg.int32(args.uploadsFrom);

        return msg;
    },
    messageAcked: () => new Message().int32(23),
    folderContentsRequest: folders => {
        folders = Array.isArray(folders) ? folders : [ folders ];
        let msg = new Message().int32(36).int32(folders.length);
        folders.forEach(folder => msg.str(folder));
        return msg;
    },
    folderContentsResponse: shareLists => {
        let zipped = new Message();

        zipped.int32(Object.keys(fileLists).length);

        Object.keys(fileLists).forEach(dir => {
                encodeList.shares(zipped, shareLists[dir], false);
        });

        zipped = zlib.deflateSync(zipped.data);

        let msg = new Message().int32(37)
        msg.writegetBuff()er(zlib.deflateSync(zipped.data));
        return msg;
    },
    */

    suspend fun transferRequest(direction: Int, token: Int, soulFile: SoulFile, fileSize: Long?) {
        val msgTransfer =
            ByteMessage()
                .writeInt32(40)
                .writeInt32(direction)
                .writeInt32(token)
                .writeStr(soulFile.filename)
        if (direction == 1)
            if (fileSize != null) msgTransfer.writeLong(fileSize)
            else {
                println("size shouldn't be null when responding transferRequest")
                return
            }
        send(msgTransfer.getBuff())
    }

    suspend fun downloadReply(ticket: Int, allowed: Boolean, filesize: Long?, reason: String?) {
        val msg = ByteMessage().writeInt32(41).writeInt32(ticket).writeBool(allowed.toInt())

        if (allowed) {
            msg.writeInt32(filesize?.toInt() ?: 0)
        } else {
            msg.writeStr(reason ?: "no reason")
        }

        send(msg.getBuff())
    }

    suspend fun queueUpload(file: SoulFile) {
        send(ByteMessage().writeInt32(43).writeStr(file.path).getBuff())
    }

    suspend fun placeInQueueRequest(filename: String) {
        send(ByteMessage().writeInt32(51).writeStr(filename).getBuff())
    }

    /*: (isUpload, token, file, size) => {
    let msg = new Message().int32(40);
    msg.int32(+isUpload); // direction 1 for upload
    msg.int32(token).str(file);

    if (isUpload) {
        msg.int64(size);
    }

    return msg;
    },*/
    /*
    transferResponse: (token, allowed, size) => {
    let msg = new Message().int32(41).int32(token);

    if (allowed) {
        msg.int8(true).int64(size);
    } else {
        msg.int8(false).str(size); // reason
    }

    return msg;
    },
    queueUpload: file => new Message().int32(43).str(file),
    placeInQueue: (file, place) => {
    return new Message().int32(44).str(file).int32(place);
    },
    uploadFailed: file => new Message().int32(46).str(file),
    queueFailed: (file, reason) => {
    return new Message().int32(50).str(file).str(reason);
    },
    // TODO may not need this
    placeInQueueRequest: file => new Message().int32(51).str(file)*/

    /* private fun setPeerSocketInterface(callback: PeerSocketInterface) {
        this.peerSocketInterface = callback
    }*/

    private fun fromUnsignedInt(value: Long): ByteArray {
        val bytes = ByteArray(8)
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putLong(value)

        return bytes.copyOfRange(4, 8)
    }
}
