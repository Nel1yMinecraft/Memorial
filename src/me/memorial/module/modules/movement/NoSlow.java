package me.memorial.module.modules.movement;

import me.memorial.events.EventTarget;
import me.memorial.events.impl.move.MotionEvent;
import me.memorial.events.impl.move.SlowDownEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.module.modules.combat.KillAura;
import me.memorial.utils.MovementUtils;
import me.memorial.value.BoolValue;
import me.memorial.value.FloatValue;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(name = "NoSlow", description = "Cancels slowness effects caused by soulsand and using items.", category = ModuleCategory.MOVEMENT)
public class NoSlow extends Module {
    private static NoSlow instance;

    public static NoSlow getInstance(){
        return instance;
    }

    public NoSlow(){
        instance = this;
    }

    private FloatValue blockForwardMultiplier = new FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F);
    private FloatValue blockStrafeMultiplier = new FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F);

    private FloatValue consumeForwardMultiplier = new FloatValue("ConsumeForwardMultiplier", 0.2F, 0.2F, 1.0F);
    private FloatValue consumeStrafeMultiplier = new FloatValue("ConsumeStrafeMultiplier", 0.2F, 0.2F, 1.0F);

    private FloatValue bowForwardMultiplier = new FloatValue("BowForwardMultiplier", 0.2F, 0.2F, 1.0F);
    private FloatValue bowStrafeMultiplier = new FloatValue("BowStrafeMultiplier", 0.2F, 0.2F, 1.0F);

    private BoolValue grim = new BoolValue("Grim", true);

    /**
     * Soulsand
     */
    // Soulsand TODO: Soulsand
    public BoolValue soulSandValue = new BoolValue("Soulsand", true); // Soulsand

    @EventTarget
    public void onMotion(MotionEvent event) {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem == null || !(heldItem.getItem() instanceof ItemSword) || !MovementUtils.isMoving()) {
            return;
        }

        KillAura killAura = KillAura.Companion.getInstance();
        if (!mc.thePlayer.isBlocking() && !killAura.getBlockingStatus()) {
            return;
        }
        if (mc.thePlayer.isBlocking() && !mc.thePlayer.isEating() && grim.get()) {
            mc.getNetHandler().getNetworkManager().sendPacket(
                    new C07PacketPlayerDigging(
                            C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                            BlockPos.ORIGIN,
                            EnumFacing.DOWN
                    )
            );
        }
    }

    @EventTarget
    public void onSlowDown(SlowDownEvent event) {
        if(mc.thePlayer.getHeldItem() == null)
            return;

        Item heldItem = mc.thePlayer.getHeldItem().getItem();

        event.setForward(getMultiplier(heldItem, true));
        event.setStrafe(getMultiplier(heldItem, false));
    }

    private float getMultiplier(Item item, boolean isForward) {
        if(item instanceof ItemFood || item instanceof ItemPotion || item instanceof ItemBucketMilk){
            return isForward ? this.consumeForwardMultiplier.get() : this.consumeStrafeMultiplier.get();
        }else if(item instanceof ItemSword){
            return isForward ? this.blockForwardMultiplier.get() : this.blockStrafeMultiplier.get();
        }else if(item instanceof ItemBow){
            return isForward ? this.bowForwardMultiplier.get() : this.bowStrafeMultiplier.get();
        }else{
            return 0.2f;
        }
    }

}
