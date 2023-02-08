package fr.sercurio.soulseekapi.utils

import fr.sercurio.soulseekapi.toHex
import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8

object Hash {
    fun toMd5(input: String): String {
        return MessageDigest.getInstance("MD5").digest(input.toByteArray(UTF_8)).toHex()
    }
}