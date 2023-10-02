package me.memorial.module.modules.world

import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.value.BoolValue

@ModuleInfo(name = "NoSlowBreak", description = "Automatically adjusts breaking speed when using modules that influence it.", category = ModuleCategory.WORLD)
class NoSlowBreak : Module() {
    companion object {
        // do i have to do this for every module bruh
        var instance: NoSlowBreak? = null
    }

    val airValue = BoolValue("Air", true)
    val waterValue = BoolValue("Water", false)

    init {
        instance = this
    }
}
