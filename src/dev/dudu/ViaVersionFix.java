/*
 * fix by elly
 * Skid form fdp
 */
package dev.dudu;

import me.memorial.events.EventTarget;
import me.memorial.events.impl.misc.PacketEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.value.BoolValue;
import net.minecraft.network.play.client.C0APacketAnimation;

@ModuleInfo(name = "ViaVersionFix", description = "1.12.2 Fix", category = ModuleCategory.EXPLOIT)
public class ViaVersionFix extends Module {
    final BoolValue swfix = new BoolValue("SwFix",true);

    @EventTarget
    void onPacket(PacketEvent event) {
        if(event.packet instanceof C0APacketAnimation && swfix.get()) {
            event.cancelEvent();
        }
    }

}