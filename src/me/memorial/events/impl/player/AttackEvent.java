package me.memorial.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.memorial.events.Event;
import net.minecraft.entity.Entity;

@Getter
public class AttackEvent extends Event {
    public Entity targetEntity;

    public AttackEvent(Entity targetEntity) {
        this.targetEntity = targetEntity;
    }
}
