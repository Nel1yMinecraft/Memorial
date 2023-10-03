package me.memorial.module.modules.movement.speeds.aac;

import me.memorial.events.impl.move.MoveEvent;
import me.memorial.module.modules.movement.speeds.SpeedMode;
import me.memorial.utils.MovementUtils;

public class AACYPort extends SpeedMode {
    public AACYPort() {
        super("AACYPort");
    }

    @Override
    public void onMotion() {
        if(MovementUtils.isMoving() && !mc.thePlayer.isSneaking()) {
            mc.thePlayer.cameraPitch = 0F;

            if(mc.thePlayer.onGround) {
                mc.thePlayer.motionY = 0.3425F;
                mc.thePlayer.motionX *= 1.5893F;
                mc.thePlayer.motionZ *= 1.5893F;
            }else
                mc.thePlayer.motionY = -0.19D;
        }
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
