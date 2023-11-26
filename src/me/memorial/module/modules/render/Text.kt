package me.memorial.module.modules.render

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
import me.memorial.value.TextValue
import java.awt.Color


@ModuleInfo("Text","ui",ModuleCategory.CLIENT)
class Text : Module() {

    val text = TextValue("Text",Memorial.CLIENT_NAME)
    val font = FontValue("Fonts",Fonts.font35)
    val rect = BoolValue("Rect",true)
    val rectaplha = FloatValue("RectAplha",150F,255F,0F)

    @EventTarget
    fun onRender2D(event : Render2DEvent) {
        if(text.get() == null) {
            text.set(Memorial.CLIENT_NAME)
        }

        if(rect.get()) {
            RenderUtils.drawRect(
                0F,
                0F,
                font.get().FONT_HEIGHT.toFloat(),
                font.get().getStringWidth(text.get()).toFloat(),
                Color(0F, 0F, 0F, rectaplha.get())
            )
        }

        font.get().drawStringWithShadow(text.get(),0F,0F,-1)


    }
}