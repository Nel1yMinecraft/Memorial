package me.memorial.cape

import com.google.gson.JsonParser
import me.memorial.Memorial
import me.memorial.utils.ClientUtils
import me.memorial.utils.MinecraftInstance
import me.memorial.utils.misc.HttpUtils
import net.minecraft.client.renderer.IImageBuffer
import net.minecraft.client.renderer.ThreadDownloadImageData
import net.minecraft.util.ResourceLocation
import java.awt.image.BufferedImage
import java.util.*
import kotlin.collections.HashMap

object CapeAPI : MinecraftInstance() {

    // Cape Service
    private var capeService: CapeService? = null

    /**
     * Register cape service
     */
    fun registerCapeService() {
        // Read cape infos from web
        val jsonObject = JsonParser()
                .parse(HttpUtils.get("${Memorial.CLIENT_CLOUD}/capes.json")).asJsonObject
        val serviceType = jsonObject.get("serviceType").asString

        // Setup service type
        when (serviceType.toLowerCase()) {
            "api" -> {
                val url = jsonObject.get("api").asJsonObject.get("url").asString

                capeService = ServiceAPI(url)
                me.memorial.utils.ClientUtils.loginfo("Registered $url as '$serviceType' service type.")
            }
            "list" -> {
                val users = HashMap<String, String>()

                for ((key, value) in jsonObject.get("users").asJsonObject.entrySet()) {
                    users[key] = value.asString
                    me.memorial.utils.ClientUtils.loginfo("Loaded user cape for '$key'.")
                }

                capeService = ServiceList(users)
                me.memorial.utils.ClientUtils.loginfo("Registered '$serviceType' service type.")
            }
        }

        me.memorial.utils.ClientUtils.loginfo("Loaded.")
    }

    /**
     * Load cape of user with uuid
     *
     * @param uuid
     * @return cape info
     */
    fun loadCape(uuid: UUID): CapeInfo? {
        // Get url of cape from cape service
        val url = (capeService ?: return null).getCape(uuid) ?: return null

        // Load cape
        val resourceLocation = ResourceLocation("capes/%s.png".format(uuid.toString()))
        val capeInfo = CapeInfo(resourceLocation)
        val threadDownloadImageData = ThreadDownloadImageData(null, url, null, object : IImageBuffer {

            override fun parseUserSkin(image: BufferedImage): BufferedImage {
                return image
            }

            override fun skinAvailable() {
                capeInfo.isCapeAvailable = true
            }

        })

        mc.textureManager.loadTexture(resourceLocation, threadDownloadImageData)

        return capeInfo
    }

    /**
     * Check if cape service is available
     *
     * @return capeservice status
     */
    fun hasCapeService() = capeService != null
}

data class CapeInfo(val resourceLocation: ResourceLocation, var isCapeAvailable: Boolean = false)