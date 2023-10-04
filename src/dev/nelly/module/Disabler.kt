package dev.nelly.module

import me.memorial.events.EventTarget
import me.memorial.events.impl.misc.PacketEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.value.TextValue
import net.minecraft.network.play.client.*

@ModuleInfo("Disabler","test",ModuleCategory.EXPLOIT)
class Disabler : Module() {
    private val tagvalue = TextValue("tag","Grim")

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is C0APacketAnimation || packet is C13PacketPlayerAbilities || packet is C08PacketPlayerBlockPlacement || packet is C07PacketPlayerDigging || packet is C02PacketUseEntity || packet is C0EPacketClickWindow || packet is C0BPacketEntityAction) {
            mc.netHandler.networkManager.sendPacket(C0FPacketConfirmTransaction())

        }
    }

    override val tag: String
        get() = tagvalue.get()
}