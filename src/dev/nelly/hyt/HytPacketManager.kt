package dev.nelly.hyt

import dev.nelly.hyt.packet.CustomPacket
import dev.nelly.hyt.packet.impl.GermModPacket
import dev.nelly.hyt.packet.impl.VexViewPacket
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo

@ModuleInfo("HytPacketManager","HytPacketManager",ModuleCategory.CLIENT)
class HytPacketManager : Module() {

    val packets: HashSet<CustomPacket> = java.util.HashSet<CustomPacket>()

    @Override
    override fun onEnable() {
        packets.add(GermModPacket())
        packets.add(VexViewPacket())
    }
}