package me.memorial.module.modules.movement;

import me.memorial.events.EventTarget;
import me.memorial.events.impl.player.UpdateEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.MovementUtils;
import me.memorial.utils.Rotation;
import me.memorial.utils.RotationUtils;
import me.memorial.value.BoolValue;
import net.minecraft.potion.Potion;

@ModuleInfo(name = "Sprint", description = "Automatically sprints all the time.", category = ModuleCategory.MOVEMENT)
public class Sprint extends Module {
    private static Sprint instance;

    public static Sprint getInstance() {
        return instance;
    }

    public Sprint(){
        instance = this;
    }

    public final BoolValue allDirectionsValue = new BoolValue("AllDirections", true);
    public final BoolValue blindnessValue = new BoolValue("Blindness", true);
    public final BoolValue foodValue = new BoolValue("Food", true);

    public final BoolValue checkServerSide = new BoolValue("CheckServerSide", false);
    public final BoolValue checkServerSideGround = new BoolValue("CheckServerSideOnlyGround", false);

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if(!MovementUtils.isMoving() || mc.thePlayer.isSneaking() ||
                (blindnessValue.get() && mc.thePlayer.isPotionActive(Potion.blindness))||
                (foodValue.get() && !(mc.thePlayer.getFoodStats().getFoodLevel() > 6.0F || mc.thePlayer.capabilities.allowFlying))
                || (checkServerSide.get() && (mc.thePlayer.onGround || !checkServerSideGround.get())
                && !allDirectionsValue.get() && RotationUtils.targetRotation != null &&
                RotationUtils.getRotationDifference(new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)) > 30)) {
            mc.thePlayer.setSprinting(false);
            return;
        }

        if(allDirectionsValue.get() || mc.thePlayer.movementInput.moveForward >= 0.8F)
            mc.thePlayer.setSprinting(true);
    }
}
