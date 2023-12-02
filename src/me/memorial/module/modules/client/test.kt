package me.memorial.module.modules.client

import me.memorial.events.EventTarget
import me.memorial.events.impl.misc.PacketEvent
import me.memorial.events.impl.player.UpdateEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.utils.ClientUtils
import net.minecraft.network.play.server.S3FPacketCustomPayload

@ModuleInfo("Test","1",ModuleCategory.CLIENT)
class test : Module() {


    @EventTarget
    fun onPacket(event : PacketEvent) {
        val packet = event.packet
        if(packet is S3FPacketCustomPayload) {
          ClientUtils.displayChatMessage(packet.channelName)
            ClientUtils.displayChatMessage(packet.bufferData.toString())
        }
    }



}