package me.memorial.module.modules.movement.speeds.spectre;

import me.memorial.events.MoveEvent;
import me.memorial.module.modules.movement.speeds.SpeedMode;
import me.memorial.utils.MovementUtils;

public class SpectreLowHop extends SpeedMode {

    public SpectreLowHop() {
        super("SpectreLowHop");
    }

    @Override
    public void onMotion() {
        if(!MovementUtils.isMoving() || mc.thePlayer.movementInput.jump)
            return;

        if(mc.thePlayer.onGround) {
            MovementUtils.strafe(1.1F);
            mc.thePlayer.motionY = 0.15D;
            return;
        }

        MovementUtils.strafe();
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
