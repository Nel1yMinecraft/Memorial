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
import me.memorial.value.*
import java.awt.Color

@ModuleInfo("Text","test",ModuleCategory.CLIENT)
class Text : Module() {
    val textValue = TextValue("Text", "Memorial")
    val shadowtext = BoolValue("ShadowText", true)
    val rect = BoolValue("Rect", true)
    val rectaplha = IntegerValue("RectAlpha", 150, 0, 255)
    val X = FloatValue("X", 2F, 0F, 200F)
    val Y = FloatValue("X", 2F, 0F, 200F)
    val fontValue = FontValue("Fonts", Fonts.font35)

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
        val text = textValue.get()

        witdh = font.FONT_HEIGHT.toFloat()
        height = font.getStringWidth(text).toFloat()
        x = X.get()
        y = Y.get()
        font.drawString(text, x, y, -1, shadowtext.get())

        if (rect.get()) {
            RenderUtils.drawRect(x, y, witdh, height, Color(0, 0, 0, rectaplha.get()))
        }

    }

}

