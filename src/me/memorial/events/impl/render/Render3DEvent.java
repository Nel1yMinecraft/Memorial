package me.memorial.events.impl.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.memorial.events.Event;
@Getter
@AllArgsConstructor
public class Render3DEvent extends Event {
    public float partialTicks;
}
