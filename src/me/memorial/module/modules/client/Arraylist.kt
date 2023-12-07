package me.memorial.module.modules.client

import jdk.nashorn.internal.objects.annotations.Getter
import me.memorial.Memorial
import me.memorial.events.EventTarget
import me.memorial.events.impl.render.Render2DEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.ui.font.Fonts
import me.memorial.utils.render.RenderUtils
import me.memorial.value.BoolValue
import me.memorial.value.FloatValue
import me.memorial.value.FontValue
import me.memorial.value.IntegerValue
import java.awt.Color

@ModuleInfo("Arraylist","test",ModuleCategory.CLIENT)
class Arraylist : Module() {
    val fontValue = FontValue("Fonts",Fonts.font35)
    val shadowtext = BoolValue("ShadowText",true)
    val rect = BoolValue("Rect",true)
    val rectaplha = IntegerValue("RectAlpha",150,0,255)
    val X = FloatValue("X",2F,0F,200F)
    var yPosition = 50F
    @get:Getter
    var witdh = 0F
    @get:Getter
    var height = 0F
    @get:Getter
    var x = 0F
    @get:Getter
    var y = 0F

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val font = fontValue.get()

        Memorial.moduleManager.modules.asSequence().filter { it.array && it.state }.toList().fold(50F) { yPosition, module ->
            val displayString = module.name
            witdh = font.FONT_HEIGHT.toFloat()
            height = font.getStringWidth(displayString).toFloat()
            x = X.get()
            y = yPosition
            font.drawString(displayString, x, y, -1, shadowtext.get())

            if(rect.get()) {
                RenderUtils.drawRoundedRect(x, y, witdh, height,2F, Color(0, 0, 0, rectaplha.get()))
            }

            yPosition + font.FONT_HEIGHT + 2F
        }

    }

}