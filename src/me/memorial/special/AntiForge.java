package me.memorial.special;

import me.memorial.events.EventTarget;
import me.memorial.events.Listenable;
import me.memorial.events.impl.misc.PacketEvent;
import me.memorial.utils.MinecraftInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class AntiForge extends MinecraftInstance implements Listenable {

    public static boolean enabled = true;
    public static boolean blockFML = true;
    public static boolean blockProxyPacket = true;
    public static boolean blockPayloadPackets = true;

    @EventTarget
    public void onPacket(PacketEvent event) {
        final Packet<?> packet = event.getPacket();

        if (enabled && !mc.isIntegratedServerRunning()) {
            try {
                if(blockProxyPacket && packet.getClass().getName().equals("net.minecraftforge.fml.common.network.internal.FMLProxyPacket"))
                    event.cancelEvent();

                if(blockPayloadPackets && packet instanceof C17PacketCustomPayload) {
                    /*final C17PacketCustomPayload customPayload = (C17PacketCustomPayload) packet;

                    if(!customPayload.getChannelName().startsWith("MC|"))
                        event.cancelEvent();
                    else if(customPayload.getChannelName().equalsIgnoreCase("MC|Brand"))
                        customPayload.data = (new PacketBuffer(Unpooled.buffer()).writeString("vanilla"));*/
                    System.out.println("???????");
                }
            }catch(final Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}