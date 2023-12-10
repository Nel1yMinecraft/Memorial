package dev.nelly.module

import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.utils.ClientUtils

@ModuleInfo("Getrotation","1",ModuleCategory.CLIENT)
class getrotation: Module() {

    override fun onEnable() {
        ClientUtils.displayChatMessage("yaw:${mc.thePlayer.rotationYaw} pitch:${mc.thePlayer.rotationPitch}")
        super.onEnable()
    }
}