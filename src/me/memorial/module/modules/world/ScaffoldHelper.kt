package me.memorial.module.modules.world

import me.memorial.Memorial
import me.memorial.events.EventTarget
import me.memorial.events.impl.player.UpdateEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.world.ScaFull


@ModuleInfo(name = "ScffoldHelper","sz", category = ModuleCategory.WORLD)
class ScaffoldHelper : Module() {
    override fun onDisable() {


    }
    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        var scaffoldmodule = Memorial.moduleManager.getModule(ScaFull::class.java) as ScaFull
        if (!mc.thePlayer!!.isSneaking){
            if (mc.thePlayer!!.onGround){
                scaffoldmodule.state = false
            }else{
                scaffoldmodule.state = true
            }

        }
    }


}