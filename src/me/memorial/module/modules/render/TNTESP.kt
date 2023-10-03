package me.memorial.module.modules.render

import me.memorial.events.EventTarget
import me.memorial.events.impl.render.Render3DEvent

import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.utils.render.RenderUtils
import net.minecraft.entity.item.EntityTNTPrimed
import java.awt.Color

@ModuleInfo(name = "TNTESP", description = "Allows you to see ignited TNT blocks through walls.", category = ModuleCategory.RENDER)
class TNTESP : Module() {

    @EventTarget
    fun onRender3D(event : Render3DEvent) {
        mc.theWorld.loadedEntityList.filterIsInstance<EntityTNTPrimed>().forEach { RenderUtils.drawEntityBox(it, Color.RED, false) }
    }
}