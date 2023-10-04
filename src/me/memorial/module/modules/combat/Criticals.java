package me.memorial.module.modules.combat;

import me.memorial.Memorial;
import me.memorial.events.impl.player.AttackEvent;
import me.memorial.events.EventTarget;
import me.memorial.events.impl.misc.PacketEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.module.modules.movement.Fly;
import me.memorial.utils.timer.MSTimer;
import me.memorial.value.IntegerValue;
import me.memorial.value.ListValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

@ModuleInfo(name = "Criticals", description = "Automatically deals critical hits.", category = ModuleCategory.COMBAT)
public class Criticals extends Module {
    public static Criticals instance;

    public ListValue modeValue = new ListValue("Mode", new String[]{"Packet", "NcpPacket", "NoGround", "Hop", "TPHop", "Jump", "LowJump"}, "packet");
    public IntegerValue delayValue = new IntegerValue("Delay", 0, 0, 500);
    private IntegerValue hurtTimeValue = new IntegerValue("HurtTime", 10, 0, 10);

    public MSTimer msTimer = new MSTimer();

    public Criticals() {
        instance = this;
    }

    @Override
    public void onEnable() {
        if (modeValue.get().equalsIgnoreCase("NoGround")) {
            mc.thePlayer.jump();
        }
    }

    @EventTarget
    public void onAttack(AttackEvent event) {
        if (event.targetEntity instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase) event.targetEntity;

            if (!mc.thePlayer.onGround || mc.thePlayer.isOnLadder() || mc.thePlayer.isInWeb || mc.thePlayer.isInWater() ||
                    mc.thePlayer.isInLava() || mc.thePlayer.ridingEntity != null || entity.hurtTime > hurtTimeValue.get() ||
                    Memorial.moduleManager.getModule(Fly.class).getState() || !msTimer.hasTimePassed(delayValue.get())) {
                return;
            }

            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;

            switch (modeValue.get().toLowerCase()) {
                case "packet":
                    mc.getNetHandler().addToSendQueue(new C04PacketPlayerPosition(x, y + 0.0625, z, true));
                    mc.getNetHandler().addToSendQueue(new C04PacketPlayerPosition(x, y, z, false));
                    mc.getNetHandler().addToSendQueue(new C04PacketPlayerPosition(x, y + 1.1E-5, z, false));
                    mc.getNetHandler().addToSendQueue(new C04PacketPlayerPosition(x, y, z, false));
                    mc.thePlayer.onCriticalHit(entity);
                    break;
                case "ncppacket":
                    mc.getNetHandler().addToSendQueue(new C04PacketPlayerPosition(x, y + 0.11, z, false));
                    mc.getNetHandler().addToSendQueue(new C04PacketPlayerPosition(x, y + 0.1100013579, z, false));
                    mc.getNetHandler().addToSendQueue(new C04PacketPlayerPosition(x, y + 0.0000013579, z, false));
                    mc.thePlayer.onCriticalHit(entity);
                    break;
                case "hop":
                    mc.thePlayer.motionY = 0.1;
                    mc.thePlayer.fallDistance = 0.1f;
                    mc.thePlayer.onGround = false;
                    break;
                case "tphop":
                    mc.getNetHandler().addToSendQueue(new C04PacketPlayerPosition(x, y + 0.02, z, false));
                    mc.getNetHandler().addToSendQueue(new C04PacketPlayerPosition(x, y + 0.01, z, false));
                    mc.thePlayer.setPosition(x, y + 0.01, z);
                    break;
                case "jump":
                    mc.thePlayer.motionY = 0.42;
                    break;
                case "lowjump":
                    mc.thePlayer.motionY = 0.3425;
                    break;
            }

            msTimer.reset();
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.packet instanceof C03PacketPlayer && modeValue.get().equalsIgnoreCase("NoGround")) {
            C03PacketPlayer packet = (C03PacketPlayer) event.packet;
            packet.onGround = false;
        }
    }

    @Override
    public String getTag() {
        return modeValue.get();
    }
}
