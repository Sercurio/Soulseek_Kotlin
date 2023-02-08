package withThreads

import java.io.DataInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SoulInputStream(val dis: DataInputStream) {
    private val tag = this.javaClass.simpleName
    var packLeft: Int = 0

    fun readAndSetMessageLength() {
        this.packLeft = this.readInt()
    }

    fun readInt(dis: DataInputStream): Int {
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

    fun checkPackLeft() {
        if (packLeft > 0) {
            println("Skipping bytes. N: $packLeft")
            dis.skipBytes(packLeft)
        }
        if (packLeft < 0) {
            println("Overrun on packet reading!")
            throw IOException("Overrun on packet reading!, packleft: $packLeft")
        }
    }
}