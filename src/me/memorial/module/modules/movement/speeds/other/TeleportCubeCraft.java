package me.memorial.module.modules.movement.speeds.other;

import me.memorial.Memorial;
import me.memorial.events.impl.move.MoveEvent;
import me.memorial.module.modules.movement.Speed;
import me.memorial.module.modules.movement.speeds.SpeedMode;
import me.memorial.utils.MovementUtils;
import me.memorial.utils.timer.MSTimer;

public class TeleportCubeCraft extends SpeedMode {

    private final MSTimer timer = new MSTimer();

    public TeleportCubeCraft() {
        super("TeleportCubeCraft");
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(final MoveEvent event) {
        if(MovementUtils.isMoving() && mc.thePlayer.onGround && timer.hasTimePassed(300L)) {
            final double yaw = MovementUtils.getDirection();
            final float length = ((Speed) Memorial.moduleManager.getModule(Speed.class)).cubecraftPortLengthValue.get();

            event.setX(-Math.sin(yaw) * length);
            event.setZ(Math.cos(yaw) * length);
            timer.reset();
        }
    }
}