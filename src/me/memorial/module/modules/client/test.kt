package me.memorial.module.modules.client

import me.memorial.events.EventTarget
import me.memorial.events.impl.render.Render2DEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.modules.impl.client.Client
import me.memorial.ui.font.Fonts
import me.memorial.utils.ClientUtils
import me.memorial.utils.render.RenderUtils
import me.memorial.utils.timer.MSTimer
import java.awt.Color

@ModuleInfo("Test","1",ModuleCategory.CLIENT)
class test : Module() {


    override fun onDisable() {
        RenderUtils.drawRect(0F,0F,Fonts.MEDIUM_35.getStringWidth("$name has Disable").toFloat(),Fonts.MEDIUM_35.FONT_HEIGHT.toFloat(),Color(20,30,150))
        MSTimer().hasTimePassed(500)
        Client().state = false
        ClientUtils.getLogger().info(Client().state)
        super.onDisable()
    }

    override fun onEnable() {
        RenderUtils.drawRect(0F,0F,
            Fonts.MEDIUM_35.getStringWidth("$name has Enable").toFloat(),
            Fonts.MEDIUM_35.FONT_HEIGHT.toFloat(),
            Color(150,30,20)
        )
        MSTimer().hasTimePassed(500)
        Client().state = true
        ClientUtils.getLogger().info(Client().state)
        super.onEnable()
    }

    @EventTarget
    fun onRender2D(event : Render2DEvent) {
        RenderUtils.drawRoundedRect(0F,0F,
            Fonts.MEDIUM_35.getStringWidth("$name has Enable").toFloat(),
            Fonts.MEDIUM_35.FONT_HEIGHT.toFloat(),
            2F,
            Color(20,150,30)
        )
        Fonts.MEDIUM_35.drawString("$name has Enable",0,0,-1)
    }



}