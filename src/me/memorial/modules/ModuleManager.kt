package me.memorial.modules

import me.memorial.modules.impl.client.Client
import me.memorial.utils.ClientUtils

object ModuleManager {

    var modules = mutableListOf<Module>()

    private fun registerModule(module: Module) {
        modules.add(module)
    }


    fun init() {
        ClientUtils.getLogger().info("[ModuleManager] Loading modules...")
        registerModule(Client())
        ClientUtils.getLogger().info("[ModuleManager] Modules loaded: ${modules.size}")
    }

}
