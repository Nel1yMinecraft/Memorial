package net.minecraft.network.play.client;

import java.io.IOException;

import dev.dudu.ViaVersionFix;
import me.memorial.Memorial;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.BlockPos;

public class C08PacketPlayerBlockPlacement implements Packet<INetHandlerPlayServer>
{
    public static final BlockPos field_179726_a = new BlockPos(-1, -1, -1);
    public BlockPos position;
    public int placedBlockDirection;
    public ItemStack stack;
    public float facingX;
    public float facingY;
    public float facingZ;

    public C08PacketPlayerBlockPlacement()
    {
    }

    public C08PacketPlayerBlockPlacement(ItemStack stackIn)
    {
        this(field_179726_a, 255, stackIn, 0.0F, 0.0F, 0.0F);
    }

    public C08PacketPlayerBlockPlacement(BlockPos positionIn, int placedBlockDirectionIn, ItemStack stackIn, float facingXIn, float facingYIn, float facingZIn)
    {
        this.position = positionIn;
        this.placedBlockDirection = placedBlockDirectionIn;
        this.stack = stackIn != null ? stackIn.copy() : null;
        this.facingX = facingXIn;
        this.facingY = facingYIn;
        this.facingZ = facingZIn;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.position = buf.readBlockPos();
        this.placedBlockDirection = buf.readUnsignedByte();
        this.stack = buf.readItemStackFromBuffer();
        final ViaVersionFix OMG = (ViaVersionFix) Memorial.moduleManager.getModule(ViaVersionFix.class);
        if (OMG.getState()) {
            this.facingX = (float)buf.readUnsignedByte();
            this.facingY = (float)buf.readUnsignedByte();
            this.facingZ = (float)buf.readUnsignedByte();
        } else {
            this.facingX = (float)buf.readUnsignedByte() / 16.0F;
            this.facingY = (float)buf.readUnsignedByte() / 16.0F;
            this.facingZ = (float)buf.readUnsignedByte() / 16.0F;
        }
    }



    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        final ViaVersionFix OMG = (ViaVersionFix) Memorial.moduleManager.getModule(ViaVersionFix.class);
        buf.writeBlockPos(this.position);
        buf.writeByte(this.placedBlockDirection);
        buf.writeItemStackToBuffer(this.stack);
        if (OMG.getState()) {
            buf.writeByte((int)(this.facingX));
            buf.writeByte((int)(this.facingY));
            buf.writeByte((int)(this.facingZ));
        } else {
            buf.writeByte((int)(this.facingX * 16.0F));
            buf.writeByte((int)(this.facingY * 16.0F));
            buf.writeByte((int)(this.facingZ * 16.0F));
        }
    }

    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processPlayerBlockPlacement(this);
    }

    public BlockPos getPosition()
    {
        return this.position;
    }

    public int getPlacedBlockDirection()
    {
        return this.placedBlockDirection;
    }

    public ItemStack getStack()
    {
        return this.stack;
    }

    public float getPlacedBlockOffsetX()
    {
        return this.facingX;
    }

    public float getPlacedBlockOffsetY()
    {
        return this.facingY;
    }

    public float getPlacedBlockOffsetZ()
    {
        return this.facingZ;
    }
}
