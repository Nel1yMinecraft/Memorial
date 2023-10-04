package me.memorial.module.modules.world;

import java.awt.Color;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import me.memorial.events.EventState;
import me.memorial.events.EventTarget;
import me.memorial.events.impl.move.MotionEvent;
import me.memorial.events.impl.render.Render3DEvent;
import me.memorial.events.impl.world.ClickBlockEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.RotationUtils;
import me.memorial.utils.block.BlockUtils;
import me.memorial.utils.render.RenderUtils;
import me.memorial.value.BoolValue;
import me.memorial.value.FloatValue;

@ModuleInfo(name = "CivBreak", description = "Allows you to break blocks instantly.", category = ModuleCategory.WORLD)
public class CivBreak extends Module {

    private BlockPos blockPos = null;
    private EnumFacing enumFacing = null;

    private final FloatValue range = new FloatValue("Range", 5F, 1F, 6F);
    private final BoolValue rotationsValue = new BoolValue("Rotations", true);
    private final BoolValue visualSwingValue = new BoolValue("VisualSwing", true);

    private final BoolValue airResetValue = new BoolValue("Air-Reset", true);
    private final BoolValue rangeResetValue = new BoolValue("Range-Reset", true);

    @EventTarget
    public void onBlockClick(ClickBlockEvent event) {
        if (BlockUtils.getBlock(event.clickedBlock) == net.minecraft.init.Blocks.bedrock)
            return;

        blockPos = event.clickedBlock;
        enumFacing = event.enumFacing;

        // Break
        Minecraft.getMinecraft().getNetHandler()
            .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, enumFacing));
        Minecraft.getMinecraft().getNetHandler()
            .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, enumFacing));
    }

    @EventTarget
    public void onUpdate(MotionEvent event) {
        BlockPos pos = blockPos;
        if (pos == null) {
            return;
        }

        if (airResetValue.get() && BlockUtils.getBlock(pos) instanceof BlockAir ||
                rangeResetValue.get() && BlockUtils.getCenterDistance(pos) > range.get()) {
            blockPos = null;
            return;
        }

        if (BlockUtils.getBlock(pos) instanceof BlockAir || BlockUtils.getCenterDistance(pos) > range.get())
            return;

        switch (event.state) {
            case PRE:
                if (rotationsValue.get()) {
                    RotationUtils.setTargetRotation(RotationUtils.faceBlock(pos));
                }
                break;
            case POST:
                if (visualSwingValue.get()) {
                    Minecraft.getMinecraft().thePlayer.swingItem();
                } else {
                    Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C0APacketAnimation());
                }

                // Break
                Minecraft.getMinecraft().getNetHandler()
                    .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                            blockPos, enumFacing));
                Minecraft.getMinecraft().getNetHandler()
                    .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                            blockPos, enumFacing));
                Minecraft.getMinecraft().playerController.clickBlock(blockPos, enumFacing);
                break;
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        RenderUtils.drawBlockBox(blockPos, Color.RED, true);
    }
}
