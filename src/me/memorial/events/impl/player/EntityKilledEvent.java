package me.memorial.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.memorial.events.Event;
import net.minecraft.entity.EntityLivingBase;

@Getter
@AllArgsConstructor
public class EntityKilledEvent extends Event {
    public EntityLivingBase targetEntity;
}
