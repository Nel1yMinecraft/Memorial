package me.memorial.module.modules.render;

import me.memorial.events.EventTarget;
import me.memorial.events.impl.misc.PacketEvent;
import me.memorial.events.impl.player.UpdateEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.MovementUtils;
import me.memorial.value.BoolValue;
import me.memorial.value.FloatValue;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;

@ModuleInfo(name = "FreeCam", description = "Allows you to move out of your body.", category = ModuleCategory.RENDER)
public class FreeCam extends Module {
    private final FloatValue speedValue = new FloatValue("Speed", 0.8F, 0.1F, 2F);
    private final BoolValue flyValue = new BoolValue("Fly", true);
    private final BoolValue noClipValue = new BoolValue("NoClip", true);

    private EntityOtherPlayerMP fakePlayer = null;
    private double oldX;
    private double oldY;
    private double oldZ;

    @Override
    public void onEnable() {
        if(mc.thePlayer == null)
            return;

        oldX = mc.thePlayer.posX;
        oldY = mc.thePlayer.posY;
        oldZ = mc.thePlayer.posZ;

        fakePlayer = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
        fakePlayer.clonePlayer(mc.thePlayer, true);

        fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
        fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);

        mc.theWorld.addEntityToWorld(-1000, fakePlayer);

        if(noClipValue.get())
            mc.thePlayer.noClip = true;
    }

    @Override
    public void onDisable() {
        if(mc.thePlayer == null || fakePlayer == null)
            return;

        mc.thePlayer.setPositionAndRotation(oldX, oldY, oldZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
        fakePlayer = null;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionZ = 0;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if(noClipValue.get())
            mc.thePlayer.noClip = true;
        mc.thePlayer.fallDistance = 0;

        if(flyValue.get()) {
            final float value = speedValue.get();

            mc.thePlayer.motionY = 0;
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
            if(mc.gameSettings.keyBindJump.isKeyDown())
                mc.thePlayer.motionY += value;
            if(mc.gameSettings.keyBindSneak.isKeyDown())
                mc.thePlayer.motionY -= value;
            MovementUtils.strafe(value);
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        final Packet<?> packet = event.getPacket();

        if(packet instanceof C03PacketPlayer || packet instanceof C0BPacketEntityAction)
            event.cancelEvent();
    }
}
