/*
 * 作者 Nelly QQ 3054086606
 * 改description死全家 改注释死全家 泄露死全家 反编译死全家
 */

package dev.nelly.module

import me.memorial.Memorial
import me.memorial.events.EventTarget
import me.memorial.events.impl.misc.PacketEvent
import me.memorial.events.impl.player.AttackEvent
import me.memorial.events.impl.player.UpdateEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.module.modules.combat.KillAura
import me.memorial.utils.ClientUtils
import me.memorial.utils.extensions.getDistanceToEntityBox
import me.memorial.utils.timer.MSTimer
import me.memorial.value.BoolValue
import me.memorial.value.FloatValue
import me.memorial.value.ListValue
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.client.C0FPacketConfirmTransaction
import net.minecraft.network.play.server.S12PacketEntityVelocity
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "Velocity2", description = "by nelly", category = ModuleCategory.COMBAT)
class Velocity2 : Module() {

    // HytMotion = NOXZ Jumprester = Legit AttackReduce = "FakeLegit" Simpe = LiquidBounce Jump = LiquidBounce
    /**
     * OPTIONS
     */
    private val horizontalValue = FloatValue("Simple-Horizontal", 0F, 0F, 1F)
    private val verticalValue = FloatValue("Simple-Vertical", 0F, 0F, 1F)
    private val modeValue =
        ListValue("Mode", arrayOf("HytMotion", "JumpRester", "AttackReduce", "Simple", "Jump"), "Simple")

    val reduceAmount = FloatValue("AttackReduce-ReduceAmount", 0.8f, 0.3f, 1f)

    //HytMotion
    private val changesource = BoolValue("HytMotion-ChangeSource", false)
    private val noblock = BoolValue("HytMotion-Noblock", false)

    // test
    private val debug = BoolValue("MotionY-Debug", false)

    /**
     * VALUES
     */
    private var velocityTimer = MSTimer()
    private var velocityInput = false

    //sb cat code
    private var velocityInput2 = false

    //attack reduce
    var attack = false
    private var jumped = 0


    override val tag: String
        get() = modeValue.get()

    override fun onDisable() {
        mc.thePlayer?.speedInAir = 0.02F
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (modeValue.get().equals("AttackReduce")) {

            if (mc.thePlayer!!.hurtTime < 3)
                return

            mc.thePlayer!!.motionX *= reduceAmount.get().toDouble()
            mc.thePlayer!!.motionZ *= reduceAmount.get().toDouble()
        }
    }


    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isInWater || thePlayer.isInLava || thePlayer.isInWeb)
            return

        when (modeValue.get().toLowerCase()) {
            "jump" -> if (thePlayer.hurtTime > 0 && thePlayer.onGround) {
                thePlayer.motionY = 0.42

                val yaw = thePlayer.rotationYaw * 0.017453292F

                thePlayer.motionX -= sin(yaw) * 0.2
                thePlayer.motionZ += cos(yaw) * 0.2
            }

            "hytmotion" -> {
                if (velocityInput) {
                    if (attack) {
                        if (!changesource.get()) {
                            mc.thePlayer!!.motionX *= 0.077760000
                            mc.thePlayer!!.motionZ *= 0.077760000
                        }
                        velocityInput = false
                        attack = false
                    } else {
                        if (mc.thePlayer!!.hurtTime > 0) {
                            mc.thePlayer!!.motionX += -1.0E-7
                            mc.thePlayer!!.motionY += -1.0E-7
                            mc.thePlayer!!.motionZ += -1.0E-7
                            mc.thePlayer!!.isAirBorne = true
                        }
                        velocityInput = false
                        attack = false
                    }
                }

                if (velocityInput2) {
                    if (mc.thePlayer!!.onGround && mc.thePlayer!!.hurtTime == 9) {
                        if (jumped > 2) {
                            jumped = 0
                        } else {
                            ++jumped
                            if (mc.thePlayer!!.ticksExisted % 5 != 0) mc.gameSettings.keyBindJump.pressed = true
                        }
                    } else if (mc.thePlayer!!.hurtTime == 8) {
                        mc.thePlayer.jump()
                        velocityInput = false
                    }
                }
            }
        }

        // 测试啊老哥 你咋给注释取消了 他妈的我刚刚玩一直有消息我以为你写的什么垃圾东西炸了
        if (debug.get() && mc.thePlayer!!.hurtTime > 0) {
            ClientUtils.displayChatMessage("MotionY:${mc.thePlayer!!.motionY}")
        }

    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val thePlayer = mc.thePlayer ?: return

        val packet = event.packet

        if (packet is S12PacketEntityVelocity) {

            if ((mc.theWorld?.getEntityByID(packet.entityID) ?: return) != thePlayer)
                return

            velocityTimer.reset()

            when (modeValue.get().toLowerCase()) {
                "simple" -> {
                    val horizontal = horizontalValue.get()
                    val vertical = verticalValue.get()

                    if (horizontal == 0F && vertical == 0F)
                        event.cancelEvent()

                    packet.motionX = (packet.motionX * horizontal).toInt()
                    packet.motionY = (packet.motionY * vertical).toInt()
                    packet.motionZ = (packet.motionZ * horizontal).toInt()
                }

                "jumprester" -> {
                    if (thePlayer.hurtTime > 0 && thePlayer.onGround && packet is S12PacketEntityVelocity) {
                        thePlayer.jump()
                    }
                }

                "hytmotion" -> {
                    velocityInput = true
                    velocityInput2 = true
                    if (mc.thePlayer!!.onGround) {
                        mc.gameSettings.keyBindJump.pressed = true
                    }
                    val aura = Memorial.moduleManager.getModule(KillAura::class.java) as KillAura
                    if (aura.state && aura.target != null && mc.thePlayer!!.getDistanceToEntityBox(aura.target!!) <= 3.01) {
                        //是否疾跑
                        if (mc.thePlayer!!.movementInput.moveForward > 0.9f && mc.thePlayer!!.isSprinting && mc.thePlayer!!.serverSprintState) {
                            repeat(5) {
                                mc.netHandler.addToSendQueue(C0FPacketConfirmTransaction(100, 100, true))
                                mc.netHandler.addToSendQueue(
                                    C02PacketUseEntity(
                                        aura.target!!,
                                        C02PacketUseEntity.Action.ATTACK
                                    )
                                )
                                mc.netHandler.addToSendQueue(C0APacketAnimation())
                            }
                            if (thePlayer.isCollidedHorizontally && noblock.get() && !thePlayer.isOnLadder && !thePlayer.isInWater && !thePlayer.isInLava) return
                            if (changesource.get()) {
                                packet.motionX = ((0.077760000 * 8000).toInt())
                                packet.motionZ = ((0.077760000 * 8000).toInt())
                            }
                            attack = true
                        } else {
                            if (mc.thePlayer!!.movementInput.moveForward > 0.9f) {
                                repeat(5) {
                                    mc.netHandler.addToSendQueue(C0FPacketConfirmTransaction(100, 100, true))
                                    mc.netHandler.addToSendQueue(
                                        C0BPacketEntityAction(
                                            mc.thePlayer,
                                            C0BPacketEntityAction.Action.START_SPRINTING
                                        )
                                    )
                                    mc.netHandler.addToSendQueue(
                                        C02PacketUseEntity(
                                            aura.target!!,
                                            C02PacketUseEntity.Action.ATTACK
                                        )
                                    )
                                    mc.netHandler.addToSendQueue(C0APacketAnimation())
                                }
                                if (thePlayer.isCollidedHorizontally && noblock.get() && !thePlayer.isOnLadder && !thePlayer.isInWater && !thePlayer.isInLava) return
                                if (changesource.get()) {
                                    packet.motionX = ((0.077760000 * 8000).toInt())
                                    packet.motionZ = ((0.077760000 * 8000).toInt())
                                }
                                attack = true
                            }
                        }
                    } else if (aura.state && aura.currentTarget != null && mc.thePlayer!!.getDistanceToEntityBox(aura.currentTarget!!) <= 3.01) {
                        //是否疾跑
                        if (mc.thePlayer!!.movementInput.moveForward > 0.9f && mc.thePlayer!!.isSprinting && mc.thePlayer!!.serverSprintState) {
                            repeat(5) {
                                mc.netHandler.addToSendQueue(C0FPacketConfirmTransaction(100, 100, true))
                                mc.thePlayer!!.sendQueue.addToSendQueue(
                                    C02PacketUseEntity(
                                        aura.target!!,
                                        C02PacketUseEntity.Action.ATTACK
                                    )
                                )
                                mc.netHandler.addToSendQueue(C0APacketAnimation())
                            }
                            if (thePlayer.isCollidedHorizontally && noblock.get() && !thePlayer.isOnLadder && !thePlayer.isInWater && !thePlayer.isInLava) return
                            if (changesource.get()) {
                                packet.motionX = ((0.077760000 * 8000).toInt())
                                packet.motionZ = ((0.077760000 * 8000).toInt())
                            }
                            attack = true
                        } else {
                            if (mc.thePlayer!!.movementInput.moveForward > 0.9f) {
                                repeat(5) {
                                    mc.netHandler.addToSendQueue(C0FPacketConfirmTransaction(100, 100, true))
                                    mc.netHandler.networkManager.sendPacket(
                                        C0BPacketEntityAction(
                                            mc.thePlayer,
                                            C0BPacketEntityAction.Action.START_SPRINTING
                                        )
                                    )
                                    mc.thePlayer!!.sendQueue.addToSendQueue(
                                        C02PacketUseEntity(
                                            aura.target!!,
                                            C02PacketUseEntity.Action.ATTACK
                                        )
                                    )
                                    mc.thePlayer!!.sendQueue.addToSendQueue(C0APacketAnimation())
                                }
                                if (thePlayer.isCollidedHorizontally && noblock.get() && !thePlayer.isOnLadder && !thePlayer.isInWater && !thePlayer.isInLava) return
                                if (changesource.get()) {
                                    packet.motionX = ((0.077760000 * 8000).toInt())
                                    packet.motionZ = ((0.077760000 * 8000).toInt())
                                }
                                attack = true
                            }
                        }
                    }
                }
            }
        }
    }
}

