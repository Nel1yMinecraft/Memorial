package me.memorial.module.modules.world;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

import me.memorial.Memorial;
import me.memorial.events.EventTarget;
import me.memorial.events.impl.move.MotionEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.module.modules.player.Blink;
import me.memorial.utils.RotationUtils;
import me.memorial.utils.block.BlockUtils;
import me.memorial.utils.timer.MSTimer;
import me.memorial.value.BlockValue;
import me.memorial.value.BoolValue;
import me.memorial.value.FloatValue;
import me.memorial.value.IntegerValue;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@ModuleInfo(name = "ChestAura", description = "Automatically opens chests around you.", category = ModuleCategory.WORLD)
public class ChestAura extends Module {

    private final FloatValue rangeValue = new FloatValue("Range", 5F, 1F, 6F);
    private final IntegerValue delayValue = new IntegerValue("Delay", 100, 50, 200);
    private final BoolValue throughWallsValue = new BoolValue("ThroughWalls", true);
    private final BoolValue visualSwing = new BoolValue("VisualSwing", true);
    private final BlockValue chestValue = new BlockValue("Chest", Block.getIdFromBlock(Blocks.chest));
    private final BoolValue rotationsValue = new BoolValue("Rotations", true);

    private BlockPos currentBlock = null;
    private final MSTimer timer = new MSTimer();

    public final ArrayList<BlockPos> clickedBlocks = new ArrayList<>();

    public Vec3 getVec(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (Objects.requireNonNull(Memorial.moduleManager.getModule(Blink.class)).getState())
            return;

        switch (event.getState()) {
            case PRE:
                if (mc.currentScreen instanceof GuiContainer)
                    timer.reset();

                float radius = rangeValue.get() + 1;

                Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(),
                        mc.thePlayer.posZ);

                currentBlock = Objects.requireNonNull(BlockUtils.searchBlocks((int) radius).entrySet().stream()
                        .filter(entry -> Block.getIdFromBlock(entry.getValue()) == chestValue.get()
                                && !clickedBlocks.contains(entry.getKey())
                                && BlockUtils.getCenterDistance(entry.getKey()) < rangeValue.get())
                        .filter(entry -> {
                            if (throughWallsValue.get())
                                return true;

                            BlockPos blockPos = entry.getKey();
                            MovingObjectPosition movingObjectPosition = mc.theWorld.rayTraceBlocks(eyesPos,
                                    getVec(blockPos), false, true, false);

                            return movingObjectPosition != null && movingObjectPosition.getBlockPos().equals(blockPos);
                        })
                        .min(Comparator.comparingDouble(entry -> BlockUtils.getCenterDistance(entry.getKey()))).orElse(null)).getKey();

                if (rotationsValue.get())
                    RotationUtils.setTargetRotation((RotationUtils.faceBlock(currentBlock == null ? null : currentBlock)
                            == null ? null : RotationUtils.faceBlock(currentBlock).getRotation()));

                break;
            case POST:
                if (currentBlock != null && timer.hasTimePassed(delayValue.get().longValue())) {
                    if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), currentBlock,
                            EnumFacing.DOWN, getVec(currentBlock))) {
                        if (visualSwing.get())
                            mc.thePlayer.swingItem();
                        else
                            mc.getNetHandler().addToSendQueue(new C0APacketAnimation());

                        clickedBlocks.add(currentBlock);
                        currentBlock = null;
                        timer.reset();
                    }
                }
                break;
        }
    }

    @Override
    public void onDisable() {
        clickedBlocks.clear();
    }
}
