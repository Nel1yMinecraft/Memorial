package me.memorial.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.memorial.events.Event;
import net.minecraft.entity.Entity;

@Getter
@AllArgsConstructor
public class EntityMovementEvent extends Event {
    public Entity movedEntity;
}
