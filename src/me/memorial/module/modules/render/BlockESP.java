package me.memorial.module.modules.render;

import me.memorial.events.EventTarget;

import me.memorial.events.impl.player.UpdateEvent;
import me.memorial.events.impl.render.Render3DEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.block.BlockUtils;
import me.memorial.utils.render.ColorUtils;
import me.memorial.utils.render.RenderUtils;
import me.memorial.utils.timer.MSTimer;
import me.memorial.value.BlockValue;
import me.memorial.value.BoolValue;
import me.memorial.value.IntegerValue;
import me.memorial.value.ListValue;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "BlockESP", description = "Allows you to see a selected block through walls.", category = ModuleCategory.RENDER)
public class BlockESP extends Module {
    private final ListValue modeValue = new ListValue("Mode", new String[] {"Box", "2D"}, "Box");

    private final BlockValue blockValue = new BlockValue("Block", 168);
    private final IntegerValue radiusValue = new IntegerValue("Radius", 40, 5, 120);

    private final IntegerValue colorRedValue = new IntegerValue("R", 255, 0, 255);
    private final IntegerValue colorGreenValue = new IntegerValue("G", 179, 0, 255);
    private final IntegerValue colorBlueValue = new IntegerValue("B", 72, 0, 255);
    private final BoolValue colorRainbow = new BoolValue("Rainbow", false);

    private final MSTimer searchTimer = new MSTimer();
    private final List<BlockPos> posList = new ArrayList<>();
    private Thread thread;

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if(searchTimer.hasTimePassed(1000L) && (thread == null || !thread.isAlive())) {
            final int radius = radiusValue.get();
            final Block selectedBlock = Block.getBlockById(blockValue.get());

            if(selectedBlock == null || selectedBlock == Blocks.air)
                return;

            thread = new Thread(() -> {
                final List<BlockPos> blockList = new ArrayList<>();

                for(int x = -radius; x < radius; x++) {
                    for(int y = radius; y > -radius; y--) {
                        for(int z = -radius; z < radius; z++) {
                            final int xPos = ((int) mc.thePlayer.posX + x);
                            final int yPos = ((int) mc.thePlayer.posY + y);
                            final int zPos = ((int) mc.thePlayer.posZ + z);

                            final BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
                            final Block block = BlockUtils.getBlock(blockPos);
                            if(block == selectedBlock)
                                blockList.add(blockPos);
                        }
                    }
                }

                searchTimer.reset();

                synchronized(posList) {
                    posList.clear();
                    posList.addAll(blockList);
                }
            }, "BlockESP-BlockFinder");
            thread.start();
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        synchronized(posList) {
            final Color color = colorRainbow.get() ? ColorUtils.rainbow() : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());

            for(final BlockPos blockPos : posList) {
                switch(modeValue.get().toLowerCase()) {
                    case "box":
                        RenderUtils.drawBlockBox(blockPos, color, true);
                        break;
                    case "2d":
                        RenderUtils.draw2D(blockPos, color.getRGB(), Color.BLACK.getRGB());
                        break;
                }
            }
        }
    }

    @Override
    public String getTag() {
        return BlockUtils.getBlockName(blockValue.get());
    }
}
