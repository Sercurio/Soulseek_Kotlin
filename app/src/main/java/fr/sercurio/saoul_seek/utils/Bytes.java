package fr.sercurio.saoul_seek.utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;


public class Bytes {
    public static String readStringLe(byte[] bytes, int start, int length) {
        byte[] stringBytes = new byte[length];
        for (int i = 0; i < length; i++) {
            stringBytes[i] = bytes[start++];
        }
        return new String(stringBytes);
    }

    public static int readInt32Le(byte[] bytes, int start) {
        byte[] intBytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            intBytes[i] = bytes[start++];
        }
        return byteArrayToLeInt(intBytes);
    }

    public static int[] unsignedToBytes(byte[] bytes) {
        int[] unsignedBytes = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++)
            unsignedBytes[i] = bytes[i] & 0xFF;
        return unsignedBytes;
    }

    public static int[] stringToUnsignedBytes(String s) {
        byte[] array = new BigInteger(s).toByteArray();
        int[] unsigned_array = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            unsigned_array[i] = array[i] >= 0 ? array[i] : array[i] + 256;
        }
        return unsigned_array;
    }

    public static byte[] concatBytesArrays(byte[] a, byte[] b) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(a);
        outputStream.write(b);

        return outputStream.toByteArray();
    }

    public static byte[] intToLittleEndian(long l) {
        byte[] b = new byte[4];
        b[0] = (byte) (l & 0xFF);
        b[1] = (byte) ((l >> 8) & 0xFF);
        b[2] = (byte) ((l >> 16) & 0xFF);
        b[3] = (byte) ((l >> 24) & 0xFF);
        return b;
    }

    public static int tokenInt() {
        byte[] b = randomBytes(4);
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static byte[] randomBytes(int n) {
        byte[] b = new byte[n];
        new Random().nextBytes(b);
        return b;
    }

    public static String md5(String input) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; ++i) {
            sb.append(Integer.toHexString((digest[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    public static int writeRawHexStr(final String hex) {
        int ret = 0;
        StringBuilder hexLittleEndian = new StringBuilder();
        if (hex.length() % 2 != 0) return ret;
        for (int i = hex.length() - 2; i >= 0; i -= 2) {
            hexLittleEndian.append(hex.substring(i, i + 2));
        }
        ret = Integer.parseInt(hexLittleEndian.toString(), 16);
        return ret;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static int toLittleEndian(final String hex) {
        int ret = 0;
        StringBuilder hexLittleEndian = new StringBuilder();
        if (hex.length() % 2 != 0) return ret;
        for (int i = hex.length() - 2; i >= 0; i -= 2) {
            hexLittleEndian.append(hex.substring(i, i + 2));
        }
        ret = Integer.parseInt(hexLittleEndian.toString(), 16);
        return ret;
    }

    public static void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    public static long readRawHexString(byte[] b, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(String.format("%02X", b[i++]));
        }
        //sb.toString()
        return Long.parseLong(sb.toString(), 16);
    }

    public static int byteArrayToLeInt(byte[] b) {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }

    public static byte[] intTo32Le(long l) {
        byte[] b = new byte[4];
        b[0] = (byte) (l & 0xFF);
        b[1] = (byte) ((l >> 8) & 0xFF);
        b[2] = (byte) ((l >> 16) & 0xFF);
        b[3] = (byte) ((l >> 24) & 0xFF);
        return b;
    }

    public static long bytesToLong(byte[] b) {
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result <<= 4;
            result |= (b[i] & 0xFF);
        }
        return result;
    }

    public static int bytesToInt(byte[] b) {
        int result = 0;
        for (int i = 4; i > 0; i--) {
            result <<= 4;
            result |= (b[i] & 0xFF);
        }
        return result;
    }

    //TODO
    /* rawHexStr (int val) {
        return this.write ? this.writeRawHexStr(val) : this.readRawHexStr(val)
    }*/

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[4];
        for (int i = 3; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= 4;
        }
        return result;
    }

    public String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(Charset.defaultCharset())));
    }

    public int read32Le(byte[] message, int start) {
        ByteBuffer bb = ByteBuffer.wrap(Arrays.copyOfRange(message, start, start + 4));

        return bb.order(ByteOrder.LITTLE_ENDIAN).getInt();
       /* byte[] intBytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            intBytes[i++] = message[start++];
        }
        return byteArrayToLeInt(intBytes);*/
    }

    /* TODO read Hex LittleEndian
        readRawHexStr (size) {
            let str = this.data.toString('hex', this.pointer, this.pointer + size)
            this.pointer += size
            return str
        }
    */
    public int read8Le(byte[] message, int start) {
        return message[start] & 0xFF;
    }
}

