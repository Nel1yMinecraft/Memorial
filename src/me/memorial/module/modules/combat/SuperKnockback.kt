package me.memorial.module.modules.combat

import me.memorial.events.impl.player.AttackEvent
import me.memorial.events.EventTarget
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.value.IntegerValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(name = "SuperKnockback", description = "Increases knockback dealt to other entities.", category = ModuleCategory.COMBAT)
class SuperKnockback : Module() {

    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase) {
            if ((event.targetEntity as EntityLivingBase).hurtTime > hurtTimeValue.get())
                return

            if (mc.thePlayer.isSprinting)
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))

            mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
            mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
            mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
            mc.thePlayer.isSprinting = true
            mc.thePlayer.serverSprintState = true
        }
    }

}