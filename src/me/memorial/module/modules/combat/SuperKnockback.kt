/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package me.memorial.module.modules.combat

import me.memorial.events.EventTarget
import me.memorial.events.impl.player.AttackEvent
import me.memorial.events.impl.player.UpdateEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.utils.MovementUtils
import me.memorial.utils.MovementUtils2
import me.memorial.utils.Rotation
import me.memorial.utils.RotationUtils
import me.memorial.utils.timer.MSTimer
import me.memorial.value.BoolValue
import me.memorial.value.IntegerValue
import me.memorial.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(name = "SuperKnockback", description = "FDP" ,category = ModuleCategory.COMBAT)
class SuperKnockback : Module() {

    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val modeValue = ListValue("Mode", arrayOf("Wtap", "Legit", "Silent", "SprintReset", "SneakPacket"), "Silent")
    private val onlyMoveValue = BoolValue("OnlyMove", true)
    private val onlyMoveForwardValue = BoolValue("OnlyMoveForward", true)
    private val onlyGroundValue = BoolValue("OnlyGround", false)
    private val delayValue = IntegerValue("Delay", 0, 0, 500)
    
    private var ticks = 0

    val timer = MSTimer()

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase) {
            if ((event.targetEntity as EntityLivingBase).hurtTime > hurtTimeValue.get() || !timer.hasTimePassed(delayValue.get().toLong()) ||
                (!MovementUtils.isMoving() && onlyMoveValue.get()) || (!mc.thePlayer.onGround && onlyGroundValue.get())) {
                return
            }
            
            if (onlyMoveForwardValue.get() && RotationUtils.getRotationDifference(Rotation(MovementUtils2.movingYaw, mc.thePlayer.rotationPitch), Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)) > 35) {
                return
            }
                
            when (modeValue.get().lowercase()) {
                
                "wtap" ->  ticks = 2
                
                
                "legit" -> {
                    ticks = 2
                }
                
                "silent" -> {
                    ticks = 1
                }

                "sprintreset" -> {
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                }
                
                "sneakpacket" -> {
                    if (mc.thePlayer.isSprinting) {
                        mc.thePlayer.isSprinting = true
                    }
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING))
                    mc.thePlayer.serverSprintState = true
                }
            }
            timer.reset()
        }
    }
    
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (modeValue.equals("Legit")) {
            if (ticks == 2) {
                mc.gameSettings.keyBindForward.pressed = false
                ticks = 1
            } else if (ticks == 1) {
                mc.gameSettings.keyBindForward.pressed = true
                ticks = 0
            }
        }
        if (modeValue.equals("Wtap")) {
            if (ticks == 2) {
                mc.thePlayer.isSprinting = false
                ticks = 1
            } else if (ticks == 1) {
                mc.thePlayer.isSprinting = true
                ticks = 0
            }
        }
        if (modeValue.equals("Silent")) {
            if (ticks == 1) {
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                ticks = 2
            } else if (ticks == 2) {
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                ticks = 0
            }
        }
    }
   
    override val tag: String
        get() = modeValue.get()
}