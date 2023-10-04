package me.memorial.module.modules.misc;

import me.memorial.events.EventTarget;
import me.memorial.events.impl.misc.PacketEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.RotationUtils;
import me.memorial.value.BoolValue;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@ModuleInfo(name = "NoRotateSet", description = "Prevents the server from rotating your head.", category = ModuleCategory.MISC)
public class NoRotateSet extends Module {

    private BoolValue confirmValue = new BoolValue("Confirm", true);
    private BoolValue illegalRotationValue = new BoolValue("ConfirmIllegalRotation", false);
    private BoolValue noZeroValue = new BoolValue("NoZero", false);

    @EventTarget
    public void onPacket(PacketEvent event) {
        Object packet = event.getPacket();

        if (mc.thePlayer == null) return;

        if (packet instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook posLookPacket = (S08PacketPlayerPosLook) packet;

            if (noZeroValue.get() && posLookPacket.yaw == 0F && posLookPacket.pitch == 0F)
                return;

            if (illegalRotationValue.get() || (posLookPacket.pitch <= 90 && posLookPacket.pitch >= -90 &&
                    RotationUtils.serverRotation != null && posLookPacket.yaw != RotationUtils.serverRotation.getYaw() &&
                    posLookPacket.pitch != RotationUtils.serverRotation.getPitch())) {

                if (confirmValue.get())
                    mc.getNetHandler().addToSendQueue(new C05PacketPlayerLook(posLookPacket.yaw, posLookPacket.pitch, mc.thePlayer.onGround));
            }

            posLookPacket.yaw = mc.thePlayer.rotationYaw;
            posLookPacket.pitch = mc.thePlayer.rotationPitch;
        }
    }
}
