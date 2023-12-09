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
        return "Memorial-$uuid-"+CaesarCipher.encrypt("${InetAddress.getLocalHost()}-${getHWID()}")
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

    fun verify2() {
        try {
            if (HttpUtils.get("https://xn--lmq31r56mnp1c.cc/user.dat.txt").contains(getTokens())) {
                Memorial.showNotification("Memorial", "Hello ${getPermissions()}")
                MinecraftInstance.mc.displayGuiScreen(MainMenu())
            } else {
                Memorial.showNotification("Memorial", " Username: ${getUUID()} -- Tokens Copying")
                Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(getTokens()), null)
            }
        } catch (e : Exception) {
         // e.printStackTrace()
        }

    }


    // 获取权限组
    fun getPermissions(): String {
        return "DEV"
    }

}