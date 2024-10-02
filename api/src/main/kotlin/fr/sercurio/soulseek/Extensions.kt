package fr.sercurio.soulseek

import java.security.MessageDigest

fun Boolean.toInt() = if (this) 1 else 0

fun ByteArray.toMD5() = joinToString(separator = "") { byte -> "%02x".format(byte) }

fun String.toMD5(): String = MessageDigest.getInstance("MD5").digest(this.toByteArray(Charsets.UTF_8)).toMD5()