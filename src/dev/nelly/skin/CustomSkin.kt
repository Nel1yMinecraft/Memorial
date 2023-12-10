package dev.nelly.skin

import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import net.minecraft.util.ResourceLocation

@ModuleInfo("CustomSkin","test",ModuleCategory.CLIENT, canEnable = false)
class CustomSkin: Module() {

    override fun onEnable() {
        SkinManager.setThePlayerskin(ResourceLocation("memorial/customskin/4dskin.png"))
        super.onEnable()
    }

}