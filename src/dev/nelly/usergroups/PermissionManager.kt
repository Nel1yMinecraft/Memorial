package dev.nelly.usergroups

import me.memorial.utils.misc.HttpUtils
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object PermissionManager {

    val cloud = "114514.com/xx.txt"
    val key = "testkey"
    // 获取网页内容
    fun get(web: String): String {
        return HttpUtils.get(web)
    }

    // 获取IP
    fun getip() : String {
        return encryption(get("https://api.ipify.org/?format=txt"))
    }

    // AES加密
    fun encryption(text: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val keySpec = SecretKeySpec(key.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    // AES解密
    fun decryption(encryptedText: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val keySpec = SecretKeySpec(key.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val encryptedBytes = Base64.getDecoder().decode(encryptedText)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }

    // 获取秘钥
    fun getKey(username: String,key: String) : String {
        return encryption("$username-${getip()}")
    }

    // 获取权限组
    fun getPermissions(tokens: String): Any? {
        if(get(cloud).equals("$tokens-DEV")) {
            return Permission.DEV
        } else if(get(cloud).equals("$tokens-USER")) {
            return Permission.USER
        }
        return null
    }

}