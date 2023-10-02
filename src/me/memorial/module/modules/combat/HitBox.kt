package me.memorial.module.modules.combat

import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.value.FloatValue

@ModuleInfo(name = "HitBox", description = "Makes hitboxes of targets bigger.", category = ModuleCategory.COMBAT)
class HitBox : Module() {
    companion object {
        var instance: HitBox? = null
    }

    init {
        instance = this
    }

    val sizeValue = FloatValue("Size", 0.4F, 0F, 1F)

}