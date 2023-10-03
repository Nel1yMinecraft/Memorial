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
    public BlockPos blockPos;
    public Block block;
    public AxisAlignedBB boundingBox;

    public int x = blockPos.getX();
    public int y = blockPos.getY();
    public int z = blockPos.getZ();

    public BlockBBEvent(BlockPos blockPos, Block block, AxisAlignedBB boundingBox) {
        this.blockPos = blockPos;
        this.block = block;
        this.boundingBox = boundingBox;
    }
}
