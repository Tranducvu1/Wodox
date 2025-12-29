package com.starnest.data.common.util

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant

object SignatureUtil {
    private fun hash(algorithm: String, key: String, encoding: String = "hex"): String {
        val digest = MessageDigest.getInstance(algorithm)
        val hashBytes = digest.digest(key.toByteArray(StandardCharsets.UTF_8))

        return hashBytes.joinToString("") { "%02x".format(it) }
    }


    fun hash(algorithm: String, kid: String, key: String, userId: String = "", validity: Int = 90): String {
        val timestamp = Instant.now().epochSecond

        val encryptKey = "$key$timestamp$validity$userId"

        val signature = mapOf(
            "kid" to kid,
            "algorithm" to algorithm,
            "timestamp" to timestamp.toString(),
            "validity" to validity.toString(),
            "userId" to userId,
            "value" to hash(algorithm, encryptKey)
        )

        return mapToQueryString(signature)
    }

    private fun mapToQueryString(map: Map<String, String>): String {
        return map.entries.joinToString("&") {
            "${URLEncoder.encode(it.key, StandardCharsets.UTF_8.toString())}=" +
                    URLEncoder.encode(it.value, StandardCharsets.UTF_8.toString())
        }
    }
}