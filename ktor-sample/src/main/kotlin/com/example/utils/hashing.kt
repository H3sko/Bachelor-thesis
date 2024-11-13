package com.example.utils

import java.security.MessageDigest
import java.security.SecureRandom

fun sha256WithSalt(password: String, salt: ByteArray): String {
    val bytes = password.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    md.update(salt)
    val digest = md.digest(bytes)
    return digest.joinToString("") { "%02x".format(it) }
}

fun generateSalt(length: Int): ByteArray {
    val random = SecureRandom()
    val salt = ByteArray(length)
    random.nextBytes(salt)
    return salt
}
   