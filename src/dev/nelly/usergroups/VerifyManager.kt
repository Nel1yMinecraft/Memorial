package dev.nelly.usergroups

import dev.nelly.ui.GuiLogin
import me.memorial.Memorial
import me.memorial.ui.client.lunar.ui.MainMenu
import me.memorial.utils.MinecraftInstance
import me.memorial.utils.misc.HttpUtils
import nellyobfuscator.NellyClassObfuscator
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


@NellyClassObfuscator
object VerifyManager {

   // 获取Tokens
    fun getTokens(uuid: String = getUUID()): String {
        return CaesarCipher.encrypt(uuid+InetAddress.getLocalHost()+getHWID())
    }

    // 获取秘钥
    fun getKey() : Int {
        return 3
    }

    //获取UUID
    fun getUUID() :  String {
        return GuiLogin.uuidInput.text
    }

    //获取HWID
    fun getHWID(): String {
        return try {
            val s = StringBuilder()
            val main = System.getenv("PROCESS_IDENTIFIER") + System.getenv("COMPUTERNAME")
            val bytes = main.toByteArray(StandardCharsets.UTF_8)
            val messageDigest = MessageDigest.getInstance("SHA")
            val sha = messageDigest.digest(bytes)
            var i = 0
            for (b in sha) {
                s.append(Integer.toHexString(b.toInt() and 0xFF or 0x300), 0, 3)
                if (i != sha.size - 1) {
                    s.append("-")
                }
                i++
            }
            s.toString()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    //发送Get请求
    fun sendGetRequest(url: String): String {
        var g = ""
        try {
            g = HttpUtils.get(url)
        } catch (e : Exception) {
            e.printStackTrace()
        }
       return g
    }

    fun verify(): String {
        var v = ""
        if (sendGetRequest("https://xn--lmq31r56mnp1c.cc/${getUUID()}.txt").contains(getTokens())) {
            v = "ililil"
        }
        if (!sendGetRequest("https://xn--lmq31r56mnp1c.cc/${getUUID()}.txt").contains(getTokens())) {
            v = "ilililil"
        }
        return v
    }

    fun verify2() {
        if (verify().contains("ililil")) {
            Memorial.showNotification("Memorial", "Hello ${getPermissions()}")
            MinecraftInstance.mc.displayGuiScreen(MainMenu())
        } else {
            Memorial.showNotification("Memorial", " UUID: ${getUUID()} -- Tokens Copying")
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(getTokens()), null)

        }
    }


    // 获取权限组
    fun getPermissions(): String {
        return "DEV"
    }

}