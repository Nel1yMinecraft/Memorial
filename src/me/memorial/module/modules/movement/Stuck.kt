package me.memorial.module.modules.movement

import me.memorial.events.EventTarget
import me.memorial.events.impl.misc.PacketEvent
import me.memorial.events.impl.move.MoveEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.value.BoolValue
import net.minecraft.network.play.client.C03PacketPlayer

@ModuleInfo("Stuck","nomove",ModuleCategory.MOVEMENT)
class Stuck: Module() {

    val cancelc03Value = BoolValue("CancelC03",true)

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if(event.packet is C03PacketPlayer && cancelc03Value.get()) {
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        event.zero()
    }
}