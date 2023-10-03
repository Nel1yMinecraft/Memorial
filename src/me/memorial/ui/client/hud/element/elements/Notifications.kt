package me.memorial.ui.client.hud.element.elements

import me.memorial.Memorial
import me.memorial.ui.client.hud.designer.GuiHudDesigner
import me.memorial.ui.client.hud.element.Border
import me.memorial.ui.client.hud.element.Element
import me.memorial.ui.client.hud.element.ElementInfo
import me.memorial.ui.client.hud.element.Side
import me.memorial.ui.font.Fonts
import me.memorial.utils.MinecraftInstance
import me.memorial.utils.render.AnimationUtils
import me.memorial.utils.render.RenderUtils
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.max

/**
 * CustomHUD Notification element
 */
@ElementInfo(name = "Notifications", single = true)
class Notifications(x: Double = 0.0, y: Double = 30.0, scale: Float = 1F,
                    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)
) : Element(x, y, scale, side) {

    /**
     * Example notification for CustomHUD designer
     */
    private val exampleNotification = Notification("Example Notification",NotifyType.INFO)

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        if (Memorial.hud.notifications.size > 0)
            Memorial.hud.notifications[0].drawNotification()

        if (mc.currentScreen is GuiHudDesigner) {
            if (!Memorial.hud.notifications.contains(exampleNotification))
                Memorial.hud.addNotification(exampleNotification)

            exampleNotification.fadeState = Notification.FadeState.STAY
            exampleNotification.x = exampleNotification.textLength + 8F

            return Border(-95F, -20F, 0F, 0F)
        }

        return null
    }

}

class Notification(private val message: String, val type: NotifyType = NotifyType.INFO) {
    var x = 0F
    var textLength = 0

    private var stay = 0F
    private var fadeStep = 0F
    var fadeState = FadeState.IN

    /**
     * Fade state for animation
     */
    enum class FadeState { IN, STAY, OUT, END }

    init {
        textLength = Fonts.font35.getStringWidth(message)
    }


    /**
     * Draw notification
     */
    fun drawNotification() {
        val font = Fonts.font35
        val width =
            100.coerceAtLeast(font.getStringWidth(message).coerceAtLeast(font.getStringWidth("Notification")) + 15)
        var img: ResourceLocation? = null
        var color: Int? = null
        // Animation
// Animation
        val delta = RenderUtils.deltaTime
        val width2 = textLength + 8F

        when (fadeState) {
            FadeState.IN -> {
                if (x < width2) {
                    x = AnimationUtils.easeOut(fadeStep, width2) * width2
                    fadeStep += delta / 4F
                }
                if (x >= width2) {
                    fadeState = FadeState.STAY
                    x = width2
                    fadeStep = width2
                }

                stay = 60F
            }

            FadeState.STAY -> if (stay > 0)
                stay = 0F
            else
                fadeState = FadeState.OUT

            FadeState.OUT -> if (x > 0) {
                x = AnimationUtils.easeOut(fadeStep, width2) * width2
                fadeStep -= delta / 4F
            } else
                fadeState = FadeState.END

            FadeState.END -> Memorial.hud.removeNotification(this)
        }
        if (type == NotifyType.WARNING) {
            img = ResourceLocation("liquidbounce/notifications/warn.png")
            color = Color(253, 252, 126).rgb
        }
        if (type == NotifyType.INFO) {
            img = ResourceLocation("liquidbounce/notifications/info.png")
            color = Color(127, 174, 210).rgb

        }
        if (type == NotifyType.SUCCESS) {
            img = ResourceLocation("liquidbounce/notifications/okay.png")
            color = Color(65, 252, 65).rgb

        }
        if (type == NotifyType.ERROR) {
            img = ResourceLocation("liquidbounce/notifications/error.png")
            color = Color(226, 87, 76).rgb

        }
        RenderUtils.drawRect(0f, 0F, width.toFloat(), 29F, Color(32, 32, 32, 200).rgb)
        RenderUtils.drawImage(img, 4, 4, 18, 18)
        Fonts.font40.drawString("Notification", 63F - 38f, 4f, Color.white.rgb, true)
        Fonts.font35.drawString(
            message,
            63f - 38f,
            4f + Fonts.font35.height + 2f + 2f,
            Color.white.rgb,
            true
        )
        RenderUtils.drawRect(
            width.toFloat(), 4f + Fonts.font35.height + 2f + Fonts.font35.height + 2f + 2f, 0F, 26f, color!!
        )
        GlStateManager.resetColor()
    }
}
enum class NotifyType(var renderColor:Color) {
    SUCCESS(Color(0x60E092)),
    ERROR(Color(0xFF2F2F)),
    WARNING(Color(0xF5FD00)),
    INFO(Color(0x6490A7));
}
