package me.memorial.module.modules.combat;

import me.memorial.events.impl.player.AttackEvent;
import me.memorial.events.EventTarget;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.value.IntegerValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C0BPacketEntityAction;

@ModuleInfo(name = "SuperKnockback", description = "Increases knockback dealt to other entities.", category = ModuleCategory.COMBAT)
public class SuperKnockback extends Module {
    private IntegerValue hurtTimeValue = new IntegerValue("HurtTime", 10, 0, 10);

    @EventTarget
    public void onAttack(AttackEvent event) {
        if (event.getTargetEntity() instanceof EntityLivingBase) {
            EntityLivingBase targetEntity = (EntityLivingBase) event.getTargetEntity();
            if (targetEntity.hurtTime > hurtTimeValue.get()) {
                return;
            }

            if (mc.thePlayer.isSprinting()) {
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }

            mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            mc.thePlayer.setSprinting(true);
        }
    }
}
