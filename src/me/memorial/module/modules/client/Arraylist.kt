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
    val fontValue = FontValue("Fonts",Fonts.MEDIUM_35)
    val rect = BoolValue("Rect",true)
    val shadow = BoolValue("Shaddow",false)
    val radius = FloatValue("Radius",6F,0F,10F)
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
            font.drawString(displayString, x, y, -1,rect.get(),font.FONT_HEIGHT.toFloat(),shadow.get(),radius.get())

            yPosition + font.FONT_HEIGHT + 2F
        }

    }

}