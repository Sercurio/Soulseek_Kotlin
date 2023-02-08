package fr.sercurio.soulseekapi

fun Boolean.toInt() = if (this) 1 else 0

fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }
