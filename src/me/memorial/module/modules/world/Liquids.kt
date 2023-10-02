package me.memorial.module.modules.world

import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo

@ModuleInfo(name = "Liquids", description = "Allows you to interact with liquids.", category = ModuleCategory.WORLD)
class Liquids : Module() {
    companion object {
        var instance: Liquids? = null
    }

    init {
        instance = this
    }
}