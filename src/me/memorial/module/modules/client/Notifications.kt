package me.memorial.module.modules.client

import me.memorial.events.EventTarget
import me.memorial.events.impl.render.Render2DEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.module.modules.client.FadeState.*
import me.memorial.ui.font.Fonts
import me.memorial.utils.ClientUtils
import me.memorial.utils.EaseUtils
import me.memorial.utils.render.RenderUtils
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.max

@ModuleInfo(name = "Notifications", description = "notif", category = ModuleCategory.CLIENT)
object Notifications : Module() {

     fun add(text: String,color: Color) {
        val width =  Fonts.font30.getStringWidth(text).toFloat()
        val height = Fonts.font30.height
        val animeTime = 500
        val nowTime = System.currentTimeMillis()
        val displayTime = System.currentTimeMillis()
        var nowY = -height
        val time = 1500
        val realY = -(0 + 1) * height
        var transY = nowY.toDouble()
        var animeXTime = System.currentTimeMillis()
        var animeYTime = System.currentTimeMillis()
        var fadeState = IN

        ClientUtils.getLogger().info("有操作好像不显示")
         ClientUtils.getLogger().info("有操作好像不显示")
         ClientUtils.getLogger().info("有操作好像不显示")
         ClientUtils.getLogger().info("有操作好像不显示")
         ClientUtils.getLogger().info("有操作好像不显示")
         ClientUtils.getLogger().info("有操作好像不显示")


         // Y-Axis Animation
        if (nowY != realY) {
            var pct = (nowTime - animeYTime) / animeTime.toDouble()
            if (pct > 1) {
                nowY = realY
                pct = 1.0
            } else {
                pct = EaseUtils.easeOutExpo(pct)
            }
            transY += (realY - nowY) * pct
        } else {
            animeYTime = nowTime
        }

        // X-Axis Animation
        var pct = (nowTime - animeXTime) / animeTime.toDouble()
        when (fadeState) {
            IN -> {
                if (pct > 1) {
                    fadeState = STAY
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = EaseUtils.easeOutExpo(pct)
            }

            STAY -> {
                pct = 1.0
                if ((nowTime - animeXTime) > time) {
                    fadeState = OUT
                    animeXTime = nowTime
                }
            }

            OUT -> {
                if (pct > 1) {
                    fadeState = END
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = 1 - EaseUtils.easeInExpo(pct)
            }

            END -> TODO()
        }

        val transX = width - (width * pct) - width
        GL11.glTranslated(transX, transY, 0.0)

        RenderUtils.drawRoundedCornerRect(
            0F + 3f,
            0F,
            max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)) + 5f, 0F),
            27f - 5f,
            2f,
            Color(0, 0, 0, 150).rgb
        )
           // if (state) Color(0x60E092).rgb else Color(0xFF2F2F).rgb
        RenderUtils.drawRoundedCornerRect(3F, 0F, width + 5f, 27f - 5f, 2f, color.rgb)
        Fonts.font35.drawCenteredString("Module", 25F, 3F, -1)
        Fonts.font30.drawCenteredString(text,30F, 12F, -1)
    }


}

