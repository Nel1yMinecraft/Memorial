package me.memorial.events.impl.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.memorial.events.impl.callables.CancellableEvent;
import net.minecraft.network.Packet;

@Getter
@AllArgsConstructor
public class PacketEvent extends CancellableEvent {
    public Packet<?> packet;
}
