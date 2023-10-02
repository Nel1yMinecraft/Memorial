package me.memorial.module.modules.movement.speeds.aac;

import me.memorial.Memorial;
import me.memorial.events.MoveEvent;
import me.memorial.module.modules.movement.Speed;
import me.memorial.module.modules.movement.speeds.SpeedMode;
import me.memorial.utils.MovementUtils;

public class AACGround2 extends SpeedMode {
    public AACGround2() {
        super("AACGround2");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {
        if(!MovementUtils.isMoving())
            return;

        mc.timer.timerSpeed = ((Speed) Memorial.moduleManager.getModule(Speed.class)).aacGroundTimerValue.get();
        MovementUtils.strafe(0.02F);
    }

    @Override
    public void onMove(MoveEvent event) {

    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1F;
    }
}
