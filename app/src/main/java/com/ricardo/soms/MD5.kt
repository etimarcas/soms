package com.example.datacollect.utilities

import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MD5 {


    fun getMD5(input: String): String? {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val messageDigest = md.digest(input.toByteArray())
            val number = BigInteger(1, messageDigest)
            var hashtext = number.toString(16)
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length < 32) {
                hashtext = "0$hashtext"
            }
            hashtext
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }
}