package me.memorial.module.modules.movement.speeds.aac;

import me.memorial.Memorial;
import me.memorial.events.MoveEvent;
import me.memorial.module.modules.movement.Speed;
import me.memorial.module.modules.movement.speeds.SpeedMode;
import me.memorial.utils.MovementUtils;
import net.minecraft.network.play.client.C03PacketPlayer;

public class AACGround extends SpeedMode {
    public AACGround() {
        super("AACGround");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {
        if(!MovementUtils.isMoving())
            return;

        mc.timer.timerSpeed = ((Speed) Memorial.moduleManager.getModule(Speed.class)).aacGroundTimerValue.get();
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
    }

    @Override
    public void onMove(MoveEvent event) {

    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
    }
}
