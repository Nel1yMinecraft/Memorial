package me.memorial.module.modules.movement;

import me.memorial.events.EventTarget;
import me.memorial.events.impl.player.UpdateEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.block.BlockUtils;
import me.memorial.value.FloatValue;
import net.minecraft.block.BlockLiquid;

@ModuleInfo(name = "WaterSpeed", description = "Allows you to swim faster.", category = ModuleCategory.MOVEMENT)
public class WaterSpeed extends Module {

    private final FloatValue speedValue = new FloatValue("Speed", 1.2F, 1.1F, 1.5F);

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if(mc.thePlayer.isInWater() && BlockUtils.getBlock(mc.thePlayer.getPosition()) instanceof BlockLiquid) {
            final float speed = speedValue.get();

            mc.thePlayer.motionX *= speed;
            mc.thePlayer.motionZ *= speed;
        }
    }
}