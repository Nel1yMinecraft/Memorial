package me.memorial.module.modules.client

import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.value.BoolValue
import me.memorial.value.ListValue

@ModuleInfo("FontSetting","client",ModuleCategory.CLIENT)
class Fonts : Module() {
    val shadowValue = ListValue("TextShadowMode", arrayOf("LiquidBounce", "Outline", "Default", "Autumn"), "Autumn")

    init {
        font = true
    }

    companion object {
        val allfonts = BoolValue("AllSFUIFont", true)
        var font = allfonts.get()
    }

}