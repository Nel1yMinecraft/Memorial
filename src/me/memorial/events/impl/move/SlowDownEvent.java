package me.memorial.events.impl.move;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.memorial.events.impl.callables.CancellableEvent;

@Getter
@Setter
@AllArgsConstructor
public class SlowDownEvent extends CancellableEvent {
    public float strafe;
    public float forward;
}
