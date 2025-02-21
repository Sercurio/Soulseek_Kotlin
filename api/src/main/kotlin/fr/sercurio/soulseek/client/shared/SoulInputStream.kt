package fr.sercurio.soulseek.client.shared

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.discardExact
import io.ktor.utils.io.readIntLittleEndian
import java.io.DataInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SoulInputStream(val byteReadChannel: ByteReadChannel) {
    var packLeft: Int = 0

    suspend fun readAndSetMessageLength() {
        packLeft = readInt()
    }

    private fun readInt(dis: DataInputStream): Int {
        return ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(dis.readInt())
            .order(ByteOrder.BIG_ENDIAN)
            .getInt(0)
    }

    suspend fun readInt(): Int {
        val g: Int = byteReadChannel.readIntLittleEndian()
        packLeft -= 4
        return g
    }

    suspend fun readBoolean(): Boolean {
        val a = byteReadChannel.readBoolean()
        packLeft--
        return a
    }

    suspend fun readString(): String {
        val length = readInt()
        val tmp = ByteArray(length)
        byteReadChannel.readFully(tmp, 0, length)
        packLeft -= length
        return String(tmp).replace("\\", "/")
    }

    fun readLong(dis: DataInputStream): Long {
        return ByteBuffer.allocate(8)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putLong(dis.readLong())
            .order(ByteOrder.BIG_ENDIAN)
            .getLong(0)
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

    suspend fun readIp(): String {
        val d: Byte = readByte()
        val c: Byte = readByte()
        val b: Byte = readByte()
        val a: Byte = readByte()
        return (if (a >= 0.toByte()) a else a + 256).toString() +
            "." +
            (if (b >= 0.toByte()) b else b + 256) +
            "." +
            (if (c >= 0.toByte()) c else c + 256) +
            "." +
            if (d >= 0.toByte()) d else d + 256
    }

    suspend fun readLong(): Long {
        val g = byteReadChannel.readLong()
        packLeft -= 8
        return ByteBuffer.allocate(8)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putLong(g)
            .order(ByteOrder.BIG_ENDIAN)
            .getLong(0)
    }

    fun readBoolean(dis: DataInputStream): Boolean {
        return dis.readBoolean()
    }

    suspend fun readByte(): Byte {
        val a = byteReadChannel.readByte()
        packLeft--
        return a
    }

    suspend fun checkPackLeft() {
        if (packLeft > 0) {
            println("Skipping bytes. N: $packLeft")
            byteReadChannel.discardExact(packLeft.toLong())
        }
        if (packLeft < 0) {
            println("Overrun on packet reading!")
            throw IOException("Overrun on packet reading!, packleft: $packLeft")
        }
    }

    suspend fun skipPackLeft() {
        byteReadChannel.discardExact(packLeft.toLong())
    }
}