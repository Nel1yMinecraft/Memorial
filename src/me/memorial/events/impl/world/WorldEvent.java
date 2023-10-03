package me.memorial.events.impl.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.memorial.events.Event;
import net.minecraft.client.multiplayer.WorldClient;

@Getter
@AllArgsConstructor
public class WorldEvent extends Event {
    public WorldClient worldClient;
}
