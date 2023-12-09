package dev.nelly.usergroups

import nellyobfuscator.NellyClassObfuscator


@NellyClassObfuscator
object CaesarCipher {
    fun test() {
        val plaintext = "Hello, world!" // 明文
        val key = 3 // 密钥

        val ciphertext = encrypt(plaintext, key) // 加密得到的密文
        println("密文：$ciphertext")

        val decryptedText = decrypt(ciphertext, key) // 解密得到的明文
        println("解密后的明文：$decryptedText")
    }

    /**
     * 加密函数
     * @param plaintext 明文字符串
     * @param key 密钥
     * @return 加密后得到的密文字符串
     */
    fun encrypt(plaintext: String, key: Int = VerifyManager.getKey()): String {
        val sb = StringBuilder()
        for (c in plaintext) {
            if (c.isLetter()) {
                val base = if (c.isUpperCase()) 'A' else 'a'
                val offset = (c - base + key) % 26
                sb.append(base + offset)
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    /**
     * 解密函数
     * @param ciphertext 密文字符串
     * @param key 密钥
     * @return 解密后得到的明文字符串
     */
    fun decrypt(ciphertext: String, key: Int = VerifyManager.getKey()): String {
        val sb = StringBuilder()
        for (c in ciphertext) {
            if (c.isLetter()) {
                val base = if (c.isUpperCase()) 'A' else 'a'
                val offset = (c - base - key + 26) % 26
                sb.append(base + offset)
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }
}
