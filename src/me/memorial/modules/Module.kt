package me.memorial.modules

import me.memorial.events.impl.player.UpdateEvent
import me.memorial.utils.MinecraftInstance
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.multiplayer.WorldClient

open class Module(name: String, tag: String? = null) : MinecraftInstance() {

    // Module
    var state: Boolean? = null
    var name: String?
    var tag: String?

    //开启时
    open fun onEnable() {
        state = true
    }

    //关闭时
    open fun onDisable() {
        state = false
    }

    open fun onUpdate(event : UpdateEvent) {
        if(state == false) {
            return
        }
    }


    init {
        this.name = name
        this.tag = tag ?: ""
    }
}
