package me.memorial.events.impl.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.memorial.events.Event;
import net.minecraft.entity.Entity;

@Getter
@AllArgsConstructor
public class RenderEntityEvent extends Event {
    public Entity entity;
    public double x,y,z;
    public float entityYaw,partialTicks;
}
