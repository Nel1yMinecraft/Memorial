package me.memorial.events.impl.move;

import lombok.Getter;
import lombok.Setter;
import me.memorial.events.EventState;
import me.memorial.events.impl.callables.CancellableEvent;

@Getter
@Setter
public class MotionEvent extends CancellableEvent {
    public float yaw;
    public float pitch;
    public double posY;
    public double posZ;
    public double posX;
    public boolean onGround;
    public EventState state;

    public MotionEvent(double posX, double posY, double posZ, float yaw, float pitch, boolean onGround, EventState state) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.state = state;
    }

    public MotionEvent(EventState state) {
        this.state = state;

    }
    public void setRotations(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
