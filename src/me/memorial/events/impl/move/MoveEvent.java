package me.memorial.events.impl.move;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.memorial.events.impl.callables.CancellableEvent;

@Getter
@Setter
@AllArgsConstructor
public class MoveEvent extends CancellableEvent {
    public double x;
    public double y;
    public double z;
    public boolean isSafeWalk;

    public MoveEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public void zero(){
        x = 0;
        y = 0;
        z = 0;
    }
    public void zeroXZ(){
        x = 0;
        z = 0;
    }
}
