package me.memorial.events.impl.world;

import lombok.Getter;
import lombok.Setter;
import me.memorial.events.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

@Getter
@Setter
public class BlockBBEvent extends Event {
    private BlockPos blockPos;
    private Block block;
    private AxisAlignedBB boundingBox;
    private int x, y, z;

    public BlockBBEvent(BlockPos blockPos, Block block, AxisAlignedBB boundingBox) {
        this.blockPos = blockPos;
        this.block = block;
        this.boundingBox = boundingBox;
        this.x = blockPos.getX();
        this.y = blockPos.getY();
        this.z = blockPos.getZ();
    }
}
