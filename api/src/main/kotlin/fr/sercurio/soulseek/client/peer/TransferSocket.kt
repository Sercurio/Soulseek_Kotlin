package fr.sercurio.soulseek.client.peer

import fr.sercurio.soulseek.client.shared.ResponseCallback
import fr.sercurio.soulseek.client.shared.AbstractSocket
import fr.sercurio.soulseek.client.peer.messages.DownloadCompleteMessage
import fr.sercurio.soulseek.client.shared.model.ByteMessage
import io.ktor.utils.io.jvm.javaio.toInputStream
import io.ktor.utils.io.readIntLittleEndian
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransferSocket(
    private val saveDirectory: String?,
    host: String,
    port: Int,
    val username: String,
    private val token: Int,
    private val filepath: String,
    private val filesize: Long,
    val downloadCompleteCallback: ResponseCallback<DownloadCompleteMessage> = ResponseCallback(),
) : AbstractSocket(host, port) {
    override suspend fun onSocketConnected() {
        pierceFirewall(token)
    }

    override suspend fun whileConnected() {
        val dir = File((saveDirectory ?: ".") + "/downloads/")
        dir.mkdirs()
        val file = File(dir.path + "/" + this.filepath.substringAfterLast("/", ""))
        if (file.exists()) file.delete()
        withContext(Dispatchers.IO) {
            //            val fileOutputStream = FileOutputStream(file)

            val buffer = ByteArray(8192)
            var offset = 0
            val token = readChannel.byteReadChannel.readIntLittleEndian()

            resumeDownload(0)

            val dis = DataInputStream(readChannel.byteReadChannel.toInputStream())
            val byteArrayOutputStream = ByteArrayOutputStream()

            while (offset < filesize) {
                val read = dis.read(buffer, 0, buffer.size)
                offset += read
                if (read > 0) {
                    offset += read
                    byteArrayOutputStream.write(buffer, 0, read)
                }
            }
            val byteArray = byteArrayOutputStream.toByteArray()

            downloadCompleteCallback.update(DownloadCompleteMessage(username, filepath, byteArray))

            byteArrayOutputStream.close()

            println("finished download of ${file.path}")
            socket.close()
        }
    }

    private suspend fun pierceFirewall(token: Int) {
        send(ByteMessage().writeInt8(0).writeInt32(token).getBuff())
    }

    private suspend fun resumeDownload(offset: Long) =
        send(ByteMessage().writeLong(offset).getBuff())

    fun onDownloadComplete(callback: (DownloadCompleteMessage) -> Unit) {
        downloadCompleteCallback.subscribe { callback(it) }
    }
}
