package me.memorial.modules.impl.client

import me.memorial.events.EventTarget
import me.memorial.events.impl.player.UpdateEvent
import me.memorial.modules.Module
import me.memorial.utils.ClientUtils

class Client : Module("Client"){

    @EventTarget
    override fun onUpdate(event : UpdateEvent) {
        ClientUtils.getLogger().info("Test")
    }

}