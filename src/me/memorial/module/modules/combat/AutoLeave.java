package me.memorial.module.modules.combat;

import me.memorial.events.EventTarget;
import me.memorial.events.impl.player.UpdateEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.value.FloatValue;
import me.memorial.value.ListValue;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Random;

@ModuleInfo(name = "AutoLeave", description = "Automatically makes you leave the server whenever your health is low.", category = ModuleCategory.COMBAT)
public class AutoLeave extends Module {
    private final FloatValue healthValue = new FloatValue("Health", 8f, 0f, 20f);
    private final ListValue modeValue = new ListValue("Mode", new String[]{"Quit", "InvalidPacket", "SelfHurt", "IllegalChat"}, "Quit");

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.getHealth() <= healthValue.get() && !mc.thePlayer.capabilities.isCreativeMode && !mc.isIntegratedServerRunning()) {
            String mode = modeValue.get().toLowerCase();
            switch (mode) {
                case "quit":
                    mc.theWorld.sendQuittingDisconnectingPacket();
                    break;
                case "invalidpacket":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !mc.thePlayer.onGround));
                    break;
                case "selfhurt":
                    mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(mc.thePlayer, C02PacketUseEntity.Action.ATTACK));
                    break;
                case "illegalchat":
                    mc.thePlayer.sendChatMessage(new ChatComponentText(String.valueOf(new Random().nextInt()) + "§§§" + new Random().nextInt()).getFormattedText());
                    break;
            }

            setState(false);
        }
    }
}