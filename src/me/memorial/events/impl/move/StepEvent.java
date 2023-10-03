package me.memorial.events.impl.move;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.memorial.events.Event;

@Getter
@Setter
@AllArgsConstructor
public class StepEvent extends Event {
    public float stepHeight;
}
