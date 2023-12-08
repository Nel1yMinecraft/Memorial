package me.memorial.modules

import me.memorial.modules.impl.client.Client
import me.memorial.utils.ClientUtils

object ModuleManager {

    var modules = mutableListOf<Module>()

    private fun registerModule(module: Module) {
        modules.add(module)
    }


    fun init() {
        me.memorial.utils.ClientUtils.loginfo("[ModuleManager] Loading modules...")
        registerModule(Client())
        me.memorial.utils.ClientUtils.loginfo("[ModuleManager] Modules loaded: ${modules.size}")
    }

}
