package me.memorial.events.impl.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.memorial.events.Event;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@Getter
@AllArgsConstructor
public class ClickBlockEvent extends Event {
    public BlockPos clickedBlock;
    public EnumFacing enumFacing;
}
