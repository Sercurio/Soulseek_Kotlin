package fr.sercurio.saoul_seek.socket

import android.util.Log
import java.io.DataInputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executors


abstract class SoulSocket(private val host: String, private val port: Int) : Runnable, SoulSocketInterface {
    private val tag = SoulSocket::class.java.name
    private lateinit var output: OutputStream
    private val executorService = Executors.newSingleThreadExecutor()

    lateinit var dis: DataInputStream
    var packLeft: Int = 0

    var connected = false

    override fun run() {
        val socket = Socket()
        try {
            socket.connect(InetSocketAddress(host, port), 1000)
            val mIn: InputStream = socket.getInputStream()
            output = socket.getOutputStream()
            dis = DataInputStream(mIn)

            onSocketConnected(this@SoulSocket)
            connected = true

            while (connected) {
                onMessageReceived()
                if (packLeft > 0) {
                    //Log.d(tag, "Class:${this.javaClass.simpleName}\nSkipping bytes. N: $packLeft")
                    dis.skipBytes(packLeft)
                }
                if (packLeft < 0) {
                    Log.d(tag, "Overrun on packet reading!")
                    connected = false
                }
            }
        } catch (e: Exception) {
            Log.d(tag,
                    "Error + $e\n" +
                            "host:${host}\nport:${port}"
            )
            onSocketDisconnected(e)
        } finally {
            socket.close()
        }
    }

    fun sendMessage(message: ByteArray) {
        executorService.submit {
            output.write(message)
            output.flush()
        }
    }

    open fun readInt(dis: DataInputStream): Int {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dis.readInt())
                .order(ByteOrder.BIG_ENDIAN).getInt(0)
    }

    fun readInt(): Int {
        val g: Int = this.dis.readInt()
        this.packLeft -= 4
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(g).order(ByteOrder.BIG_ENDIAN).getInt(0)
    }

    fun readBoolean(): Boolean {
        val a = dis.readBoolean()
        packLeft--
        return a
    }

    fun readString(): String {
        val length = readInt()
        val tmp = ByteArray(length)
        dis.readFully(tmp, 0, length)
        packLeft -= length
        return String(tmp).replace("\\", "/")
    }


    fun readLong(dis: DataInputStream): Long {
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(dis.readLong())
                .order(ByteOrder.BIG_ENDIAN).getLong(0)
    }

    fun readByte(dis: DataInputStream): Byte {
        return dis.readByte()
    }

    fun readString(dis: DataInputStream): String {
        val length = readInt(dis)
        val tmp = ByteArray(length)
        dis.readFully(tmp, 0, length)
        return String(tmp).replace("\\", "/")
    }

    fun readIp(): String {
        val d: Byte = readByte()
        val c: Byte = readByte()
        val b: Byte = readByte()
        val a: Byte = readByte()
        return (if (a >= 0.toByte()) a else a + 256).toString() + "." + (if (b >= 0.toByte()) b else b + 256) + "." + (if (c >= 0.toByte()) c else c + 256) + "." + if (d >= 0.toByte()) d else d + 256
    }


    fun readLong(): Long {
        val g = dis.readLong()
        packLeft -= 8
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(g).order(ByteOrder.BIG_ENDIAN)
                .getLong(0)
    }

    fun readBoolean(dis: DataInputStream): Boolean {
        return dis.readBoolean()
    }


    fun readByte(): Byte {
        val a = dis.readByte()
        packLeft--
        return a
    }

    fun stop() {
        connected = false
    }
}

interface SoulSocketInterface {
    fun onSocketConnected(soulSocket: SoulSocket)
    fun onSocketDisconnected(exception: Exception)
    fun onMessageReceived()
}
