package fr.sercurio.soulseek.entities

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

class ByteMessage {
    private var data: ByteArray = ByteArray(0)
    private lateinit var bb: ByteBuffer
    private var pointer: Int = 0
    private val intSize = 4

    fun writeInt8(value: Int): ByteMessage {
        val b = ByteArray(1)
        b[0] = (value and 0xFF).toByte()
        bb = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN)


        data += bb.array()
        pointer += 1
        return this
    }

    /*
    /**
     * write 8bits integer concatenated with the current ByteKot
     */
    fun writeInt8(value: Int): ByteMessage {
        bb = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN).putInt(value)
        data += bb.array()
        this.pointer += 1
        return this
    }
    */

    /**
     * write 32bits integer concatenated with the current ByteKot
     */
    fun writeInt32(value: Int): ByteMessage {
        bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value)
        data += bb.array()
        this.pointer += 4
        return this
    }

    /**
     * write 64bits long concatenated with the current ByteKot
     */
    fun writeLong(value: Long): ByteMessage {
        bb = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value)
        data += bb.array()
        this.pointer += 8
        return this
    }

    fun writeStr(str: String): ByteMessage {
        val strBytes = str.toByteArray(StandardCharsets.ISO_8859_1)

        bb = ByteBuffer.allocate(intSize + strBytes.size).order(ByteOrder.LITTLE_ENDIAN)
        bb.putInt(strBytes.size)
        bb.put(strBytes)

        data += bb.array()
        pointer += strBytes.size

        return this
    }


    fun writeBool(value: Int): ByteMessage {
        bb = ByteBuffer.allocate(1).order(ByteOrder.LITTLE_ENDIAN).put(value.toByte())
        data += bb.array()
        this.pointer += 1
        return this
    }

    /*
    fun writeRawHexStr(str: String): ByteMessage? {
        val baos = ByteArrayOutputStream()
        val hexBytes = String.format("%040x", BigInteger(1, str.toByteArray())).toByteArray()
        try {
            baos.write(data)
            baos.write(hexBytes)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        data = baos.toByteArray()
        pointer += hexBytes.size
        return this
    }
    */

    fun getBuff(): ByteArray {
        bb = ByteBuffer.allocate(intSize + data.size).order(ByteOrder.LITTLE_ENDIAN)

        bb.putInt(data.size)
        bb.put(data)

        return bb.array()
    }

    fun writeRawBytes(bytes: ByteArray): ByteMessage {
        bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

        data += bb.array()
        pointer += bytes.size

        return this
    }
}