package fr.sercurio.saoul_seek.socket

import android.util.Log
import fr.sercurio.saoul_seek.message.ByteMessage
import fr.sercurio.saoul_seek.models.Peer
import fr.sercurio.saoul_seek.models.SoulFile
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream
import kotlin.random.Random

abstract class PeerSocket(val peer: Peer) : SoulSocket(peer.host, peer.port), PeerSocketInterface {
    private val tag = this.javaClass.simpleName
    //private lateinit var peerSocketInterface: PeerSocketInterface

    override fun onMessageReceived() {
        synchronized(this) {
            packLeft = readInt()
            val code = readInt()
            Log.d(tag, "Received: Message code:" + code + " Packet Size:" + (this.packLeft + 4))
            when (code) {
                4 -> receiveSharesRequest()
                5 -> receiveSharesReply()
                8 -> receiveSearchRequest()
                9 -> receiveSearchReply()
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
        }
    }


    fun receiveSharesRequest() {
        Log.d(tag, "received share request")
        //sendSharesReply()
    }


    fun receiveSharesReply() {
        val dataInputStream = DataInputStream(InflaterInputStream(dis))
        Log.d(tag, "Loading " + this.peer.username + " shares.")
        val nDirs = readInt(dataInputStream)
        //val hashMap: HashMap<String?, ShareDirectory?> = HashMap<Any?, Any?>(nDirs)
        Log.d(tag, "Loading $nDirs folders.")
        for (i in 0 until nDirs) {
            val dirName = readString(dataInputStream)
            /*val parent: ShareDirectory? = hashMap[Util.getFolderPath(dirName)] as ShareDirectory?
            val currentDir = ShareDirectory(dirName, parent)
            hashMap[dirName] = currentDir
            if (parent == null) {
                this.service.rootShare.put(this.peerName, currentDir)
            }*/
            val nFiles = readInt(dataInputStream)
            for (j in 0 until nFiles) {
                readByte(dataInputStream)
                val filename = readString(dataInputStream)
                val fileSize = readLong(dataInputStream)
                readString(dataInputStream)
                val nAttributes = readInt(dataInputStream)
                var bitrate = 0
                var length = 0
                var vbr = 0
                for (k in 0 until nAttributes) {
                    when (readInt(dataInputStream)) {
                        0 -> bitrate = readInt(dataInputStream)
                        1 -> length = readInt(dataInputStream)
                        2 -> vbr = readInt(dataInputStream)
                        else -> readInt(dataInputStream)
                    }
                }
                //val shareFile = ShareFile(currentDir, filename, filesize, bitrate, length, vbr)
                Log.d(tag, "decodeFiles: $dirName, $filename")
            }
        }
        Log.d(tag, "Finished Loading " + this.peer.username + " shares.")
    }


    fun receiveSearchRequest() {
        val ticket = readInt()
        val query = readString()
        /*val cursor: Cursor = GoSeekData.searchShares(query)
        if (cursor != null) {
            sendSearchReply(ticket, query, cursor)
        }*/
    }


    private fun receiveSearchReply() {
        val soulFiles = arrayListOf<SoulFile>()

        val dataInputStream = DataInputStream(InflaterInputStream(this.dis))

        val user = readString(dataInputStream)
        val ticket = readInt(dataInputStream)
        var path: String = ""
        var size: Long = 0
        var extension: String = ""
        var bitrate: Int = 0
        var duration: Int = 0
        var vbr: Int = 0
        var slotsFree = true
        var avgSpeed = 0
        var queueLength: Long = 0
        if (true /*TODO search the ticket*/) {
            val nResults = readInt(dataInputStream)
            for (i in 0 until nResults) {
                val unused = readBoolean(dataInputStream) //unused
                path = readString(dataInputStream)
                size = readLong(dataInputStream)
                extension = readString(dataInputStream)
                val nAttr = readInt(dataInputStream)
                for (j in 0 until nAttr) {
                    when (val posAttr = readInt(dataInputStream)) {
                        0 -> bitrate = readInt(dataInputStream)
                        1 -> duration = readInt(dataInputStream)
                        2 -> vbr = readInt(dataInputStream)
                        else -> readInt(dataInputStream)
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
                soulFiles.add(SoulFile(path, filename, folderPath, folder, size, extension, bitrate, vbr, duration))
            }
            slotsFree = readBoolean(dataInputStream)
            avgSpeed = readInt(dataInputStream)
            queueLength = readLong(dataInputStream)

            peer.soulFiles = soulFiles
            peer.slotsFree = slotsFree
            peer.avgSpeed = avgSpeed
            peer.queueLength = queueLength
            Log.d(tag, "Received " + nResults + " search results from ${this.peer.username}\n soulfiles : ${peer.soulFiles}")

            if (!peer.soulFiles.isNullOrEmpty())
                onAbstractFileSearchResult(peer)
        }
    }

    fun receiveInfoRequest() {}


    fun receiveInfoReply() {
        val description = readString()
        if (readBoolean()) {
            val picture = readString()
        }
        val totalupl = readInt()
        val queuesize = readInt()
        val slotsfree = readInt()
        Log.d(tag, "Received User Info Reply.")
        /*val activity: Activity = Util.uiActivity
        if (activity.getClass() === ProfileActivity::class.java && (activity as ProfileActivity).peerName.equals(this.peerName)) {
            (activity as ProfileActivity).updateProfile(description)
        }*/
    }


    fun receiveFolderContentsRequest() {
        val nFiles = readInt()
        val file = arrayOfNulls<String>(nFiles)
        for (i in 0 until nFiles) {
            file[i] = readString()
        }
        Log.d(tag, "Received Folder Contents Request.")
        //sendFolderContentsReply(file)
    }


    fun receiveFolderContentsReply() {
        /*var i: Int
        val dataInputStream = DataInputStream(InflaterInputStream(dis))
        var nFolders = readInt(dataInputStream)
        i = 0
        while (i < nFolders) {
            readString(dataInputStream)
            i++
        }
        nFolders = readInt(dataInputStream)
        i = 0
        while (i < nFolders) {
            val dir = readString(dataInputStream)
            val nFiles = readInt(dataInputStream)
            Log.d(tag, "Parsing " + dir + " from:" + this.peerName + ".")
            var currentDir: ShareDirectory? = this.service.rootShare.get(this.peerName) as ShareDirectory
            if (currentDir == null) {
                currentDir = ShareDirectory(dir, null)
                this.service.rootShare.put(this.peerName, currentDir)
            } else {
                currentDir = currentDir.open(dir)
            }
            for (j in 0 until nFiles) {
                readByte(dataInputStream)
                val file = readString(dataInputStream)
                val size = readLong(dataInputStream)
                val ext = readString(dataInputStream)
                val nAttrs = readInt(dataInputStream)
                var bitrate = 0
                var length = 0
                var vbr = 0
                for (k in 0 until nAttrs) {
                    when (readInt(dataInputStream)) {
                        0 -> bitrate = readInt(dataInputStream)
                        1 -> length = readInt(dataInputStream)
                        2 -> vbr = readInt(dataInputStream)
                        else -> readInt(dataInputStream)
                    }
                }
                val shareFile = ShareFile(currentDir, file, size, bitrate, length, vbr)
            }
            i++
        }
        Log.d(tag, "Finished Loading " + this.peerName.toString() + " folder reply.")
        val a: Activity = Util.uiActivity
        if (a != null && a.getClass() === BrowsePeerActivity::class.java) {
            (a as BrowsePeerActivity).update(this.peerName)
        }*/
    }


    fun receiveTransferRequest() {
        /*val direction = readInt()
        val ticket = readInt()
        val path = readString()
        var filesize: Long = 0
        if (direction == 1) {
            filesize = readLong()
        }
        if (direction == 1) {
            Log.d(tag, "Peer:" + this.peerName.toString() + " wants to send us a file:" + path)
            val cursor: Cursor = GoSeekData.getDownload(this.peerName, path)
            if (cursor != null && cursor.getCount() > 0) {
                Log.d(tag, "The file is recognized as a download we requested.")
                GoSeekData.updateDownloadTicketAndFileSize(this.peerName, path, ticket, filesize)
                if (5 == 7 || 5 == 5 || 5 == 11 || 5 == 12 || 5 == 14 || 5 == 15) {
                    Log.d(tag, "Sending a confirmation to start the transfer.")
                    sendDownloadReply(ticket, true, null)
                    return
                }
                sendDownloadReply(ticket, false, "Cancelled")
                return
            } else if (GoSeekData.isUserTrusted(this.peerName)) {
                Log.d(tag, "A Trusted user is uploading a file to us.")
                GoSeekData.newDownload(ticket, Util.getFilename(path), this.peerName, path, 5, 0, filesize, 0, 0, 0)
                Log.d(tag, "Sending a confirmation to start the transfer.")
                sendDownloadReply(ticket, true, null)
                return
            } else {
                sendDownloadReply(ticket, false, "Forbidden.")
                Log.d(tag, "Unsolicited upload attempted at us.")
                return
            }
        }
        Log.d(tag, "Peer:" + this.peerName.toString() + " wants to download a file:" + path)*/
    }


    private fun receiveTransferReply() {
        /*val ticket = readInt()
        val allowed = readBoolean()
        if (allowed && packLeft >= 8) {
            readLong()
        } else if (!allowed) {
            Log.d(tag, "Not Allowed:" + readString())
        }
        Log.d(tag, "Received transfer reply.")
        if (allowed) {
            this.service.openUploadSocket(this.peerName, this.address, port, ticket)
            return
        }
        val goSeekService: GoSeekService = this.service
        goSeekService.pendingUploads--*/
    }


    private fun receiveQueueDownload() {
        val filename = readString()
        /* Log.d(tag, "Received a queue download request.")
         this.service.queueUpload(this.peerName, filename)*/
    }


    private fun receivePlaceInQueueReply() {
        /* GoSeekData.updateDownloadPlace(this.peerName, readString(), readInt())
         val a: Activity = Util.uiActivity
         if (a.getClass() === TransfersActivity::class.java) {
             (a as TransfersActivity).update()
         }*/
    }

    private fun receiveUploadFailed() {}


    private fun receiveQueueFailed() {
        /*val path = readString()
        Log.d(tag, "Queue Failed. Reason: " + readString())
        GoSeekData.updateDownloadState(this.peerName, path, 13)*/
    }


    private fun receivePlaceInQueueRequest() {
        /*val filename = readString()
        val c: Cursor = GoSeekData.getUpload(this.peerName, filename)
        if (c != null && c.getCount() > 0) {
            c.moveToFirst()
            sendPlaceInQueueReply(filename, c.getInt(c.getColumnIndex("place")))
        }*/
    }

    private fun receiveUploadQueueNotification() {}

    fun deflate(data: ByteArray): ByteArray? {
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

    fun pierceFirewall(token: Long) {
        sendMessage(ByteMessage().writeInt8(0).writeInt32(token.toInt()).getBuff())
    }

    fun peerInit(username: String, connectionType: String, token: Long) {
        sendMessage(ByteMessage().writeInt8(1)
                .writeStr(username)
                .writeStr(connectionType) //.writeInt(300)
                .writeRawBytes(fromUnsignedInt(token))
                .getBuff())
    }

    fun getShareFileList() {
        sendMessage(ByteMessage()
                .writeInt8(4)
                .getBuff())
    }

    fun fileSearchRequest(token: Long, query: String) {
        sendMessage(ByteMessage()
                .writeInt8(8)
                .writeRawBytes(Random.Default.nextBytes(4))
                .writeStr(query)
                .getBuff())
    }

    fun userInfoRequest() {
        sendMessage(ByteMessage().writeInt8(15).getBuff())
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
    fun transferRequest(direction: Int, token: Long, filename: String, fileSize: Long?) {
        val msgInit = ByteMessage().writeInt8(1)
                .writeStr(this.peer.username)
                .writeStr("P") //.writeInt(300)
                .writeRawBytes(fromUnsignedInt(token))

        val msgTransfer = ByteMessage()
                .writeInt8(40)
                .writeInt32(direction)
                .writeLong(token)
                .writeStr(filename)
        if (direction == 1)
            if (fileSize != null)
                msgTransfer.writeLong(fileSize)
            else {
                Log.e(tag, "size shouldn't be null when responding transferRequest")
                return;
            }
        sendMessage(msgInit.getBuff() + msgTransfer.getBuff())
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

/*fun setPeerSocketInterface(callback: PeerSocketInterface) {
    this.peerSocketInterface = callback
}*/

    fun fromUnsignedInt(value: Long): ByteArray {
        val bytes = ByteArray(8);
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putLong(value);

        return bytes.copyOfRange(4, 8);
    }

}

interface PeerSocketInterface {
    fun onAbstractGetSharedList()
    fun onAbstractFileSearchResult(peer: Peer)
    fun onAbstractFolderContentsRequest(numberOfFiles: Int)
    fun onAbstractTransferDownloadRequest(token: Long, allowed: Int, reason: String?)
    fun onAbstractUploadFailed(filename: String)
    fun onAbstractQueueFailed(filename: String?, reason: String)
}