@file:Suppress("unused")

package com.caidt.util

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

object AES {

  private const val KEY_ALGORITHM = "AES"
  private const val CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding" //默认的加密算法 CBC need IV

  // 加密
  fun encrypt(bytes: ByteArray, secretKeySpec: SecretKeySpec): ByteArray {
    val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
    return cipher.doFinal(bytes)
  }

  // 加密
  fun encrypt(bytes: ByteArray, key: String): ByteArray {
    val secretKeySpec = generateKey(key)
    val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
    return cipher.doFinal(bytes)
  }

  // 解密
  fun decrypt(bytes: ByteArray, secretKeySpec: SecretKeySpec): ByteArray {
    val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
    return cipher.doFinal(bytes)
  }

  // 解密
  fun decrypt(bytes: ByteArray, key: String): ByteArray {
    val secretKeySpec = generateKey(key)
    val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
    return cipher.doFinal(bytes)
  }

  // 生成随机密钥
  fun generateKey(): SecretKeySpec {
    val generator = KeyGenerator.getInstance(KEY_ALGORITHM)
    generator.init(128)
    return SecretKeySpec(generator.generateKey().encoded, KEY_ALGORITHM)
  }

  // 生成密钥
  fun generateKey(key: String): SecretKeySpec {
    val generator = KeyGenerator.getInstance(KEY_ALGORITHM)
    generator.init(128, SecureRandom(key.toByteArray()))
    return SecretKeySpec(generator.generateKey().encoded, KEY_ALGORITHM)
  }

}
