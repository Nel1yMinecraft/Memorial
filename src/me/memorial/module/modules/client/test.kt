package me.memorial.module.modules.client

import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.modules.impl.client.Client
import me.memorial.utils.ClientUtils

@ModuleInfo("Test","1",ModuleCategory.CLIENT)
class test : Module() {


    override fun onDisable() {
        Client().state = false
        ClientUtils.displayChatMessage(state.toString())
        super.onDisable()
    }

    override fun onEnable() {
        Client().state = true
        ClientUtils.displayChatMessage(state.toString())
        super.onEnable()
    }



}