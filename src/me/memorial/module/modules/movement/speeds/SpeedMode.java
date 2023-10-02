package me.memorial.module.modules.movement.speeds;

import me.memorial.Memorial;
import me.memorial.events.MoveEvent;
import me.memorial.module.modules.movement.Speed;
import me.memorial.utils.MinecraftInstance;

public abstract class SpeedMode extends MinecraftInstance {

    public final String modeName;

    public SpeedMode(final String modeName) {
        this.modeName = modeName;
    }

    public boolean isActive() {
        final Speed speed = ((Speed) Memorial.moduleManager.getModule(Speed.class));

        return speed != null && !mc.thePlayer.isSneaking() && speed.getState() && speed.modeValue.get().equals(modeName);
    }

    public abstract void onMotion();

    public abstract void onUpdate();

    public abstract void onMove(final MoveEvent event);

    public void onTick() {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }
}
