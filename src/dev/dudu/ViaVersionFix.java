/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package dev.dudu;


import me.memorial.events.impl.misc.PacketEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.value.BoolValue;
import net.minecraft.network.play.client.C0APacketAnimation;

@ModuleInfo(name = "ViaVersionFix", description = "1.12.2 Fix", category = ModuleCategory.EXPLOIT)
public class ViaVersionFix extends Module {
    private final BoolValue swingfixvalue = new BoolValue("SwingFix", true);
    public void onPacket(final PacketEvent event){
        if(event.getPacket() instanceof C0APacketAnimation && swingfixvalue.get()) {
            event.cancelEvent();
        }
    }
}