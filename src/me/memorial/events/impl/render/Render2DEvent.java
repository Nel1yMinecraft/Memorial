package me.memorial.events.impl.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.memorial.events.Event;
import net.minecraft.client.gui.ScaledResolution;
@Getter
@AllArgsConstructor
public class Render2DEvent extends Event {
    public float partialTicks;
    public ScaledResolution scaledResolution;
}
