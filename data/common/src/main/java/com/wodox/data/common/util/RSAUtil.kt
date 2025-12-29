package com.starnest.data.common.util

import android.content.Context
import android.os.Build
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


class RSAUtil private constructor(val context: Context) {
    private var publicKey: String? = null
    private var privateKey: String? = null

    init {
        publicKey = context.assets.open("keys/public_key").bufferedReader().use { it.readText() }
            .replace("-----BEGIN PUBLIC KEY-----\n", "")
            .replace("\n-----END PUBLIC KEY-----", "")
        privateKey = context.assets.open("keys/private_key").bufferedReader().use { it.readText() }
            .replace("-----BEGIN RSA PRIVATE KEY-----\n", "")
            .replace("\n-----END RSA PRIVATE KEY-----", "")
    }

    companion object {
        @JvmStatic
        fun getInstance(context: Context) = RSAUtil(context)
    }

    fun encrypt(data: String): String {
        return try {
            val publicKeySpec = X509EncodedKeySpec(decryptBASE64(publicKey))
            val keyFactory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                KeyFactory.getInstance("RSA")
            } else {
                KeyFactory.getInstance("RSA", "BC")
            }
            val publicKey = keyFactory.generatePublic(publicKeySpec)
            val cipher = Cipher.getInstance("RSA/None/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            encryptBASE64(cipher.doFinal(data.toByteArray())) ?: data
        } catch (e: Exception) {
            e.printStackTrace()
            data
        }
    }

    fun decrypt(data: String): String {
        return try {
            val bytes = decryptBASE64(data)
            val privateKeySpec = PKCS8EncodedKeySpec(decryptBASE64(privateKey))
            val keyFactory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                KeyFactory.getInstance("RSA")
            } else {
                KeyFactory.getInstance("RSA", "BC")
            }
            val privateKey = keyFactory.generatePrivate(privateKeySpec)
            val cipher = Cipher.getInstance("RSA/None/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            String(cipher.doFinal(bytes), StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            data
        }
    }

    private fun decryptBASE64(key: String?): ByteArray? {
        return Base64.decode(key, Base64.NO_WRAP)
    }

    private fun encryptBASE64(key: ByteArray?): String? {
        return Base64.encodeToString(key, Base64.NO_WRAP)
    }
}