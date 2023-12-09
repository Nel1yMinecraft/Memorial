package me.memorial.module.modules.client

import me.memorial.events.EventTarget
import me.memorial.events.impl.render.Render2DEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.ui.font.Fonts
import me.memorial.utils.render.RenderUtils
import me.memorial.value.FloatValue
import me.memorial.value.FontValue
import me.memorial.value.IntegerValue
import me.memorial.value.ListValue
import java.awt.Color

@ModuleInfo("GameInfo","Memorial",ModuleCategory.CLIENT)
class GameInfo: Module() {

    val mode = ListValue("ModeValue", arrayOf("Normal","String"),"Normal")
    val X = FloatValue("X",2F,0F,500F)
    val Y = FloatValue("Y",2F,0F,500F)
    val radius = FloatValue("Radius",2F,0F,10F)
    val aplha = IntegerValue("RectAplha",150,0,255)
    val font = FontValue("Fonts",Fonts.MEDIUM_35)
    val x = X.get()
    val y = Y.get()
    var witdh = 0F
    var height = 0F
    val fonts = font.get()

    @EventTarget
    fun onRender2D(event : Render2DEvent) {

            if(mode.get().equals("Nomral"))  {
                witdh = 30F
                height = 40F
                RenderUtils.drawRoundedRect(x,y,witdh,height,radius.get(), Color(0,0,0,aplha.get()))
            }
        }


}