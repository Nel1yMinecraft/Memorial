package me.memorial.module.modules.movement

import me.memorial.events.EventState
import me.memorial.events.EventTarget
import me.memorial.events.impl.misc.PacketEvent
import me.memorial.events.impl.move.MotionEvent
import me.memorial.events.impl.move.SlowDownEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.module.modules.combat.KillAura
import me.memorial.utils.MovementUtils
import me.memorial.value.BoolValue
import me.memorial.value.FloatValue
import net.minecraft.block.Block
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.item.*
import net.minecraft.network.Packet
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.S30PacketWindowItems
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MovingObjectPosition

@ModuleInfo(
    name = "NoSlow",
    description = "Cancels slowness effects caused by soulsand and using items.",
    category = ModuleCategory.MOVEMENT
)
class NoSlow : Module() {
    private val blockForwardMultiplier = FloatValue("BlockForwardMultiplier", 1.0f, 0.2f, 1.0f)
    private val blockStrafeMultiplier = FloatValue("BlockStrafeMultiplier", 1.0f, 0.2f, 1.0f)
    private val consumeForwardMultiplier = FloatValue("ConsumeForwardMultiplier", 0.2f, 0.2f, 1.0f)
    private val consumeStrafeMultiplier = FloatValue("ConsumeStrafeMultiplier", 0.2f, 0.2f, 1.0f)
    private val bowForwardMultiplier = FloatValue("BowForwardMultiplier", 0.2f, 0.2f, 1.0f)
    private val bowStrafeMultiplier = FloatValue("BowStrafeMultiplier", 0.2f, 0.2f, 1.0f)
    private val grim = BoolValue("Grim", false)
    private val grim2 = BoolValue("Grim2", false)
    // 木糖醇
    private val grim3 = BoolValue("Grim3", true)

    /**
     * Soulsand
     */
    // Soulsand TODO: Soulsand
    @JvmField
    var soulSandValue = BoolValue("Soulsand", true) // Soulsand

    init {
        instance = this
    }

    @EventTarget
    fun onPacket(event : PacketEvent) {
        if (isHoldingPotionAndSword(
                mc.thePlayer.heldItem,
                false,
                true
            ) && event.packet is S30PacketWindowItems
        ) {
            event.setCancelled(true)
        }

        if (isHoldingPotionAndSword(
                mc.thePlayer.heldItem,
                false,
                true
            )
        ) {
            if (event.packet is C08PacketPlayerBlockPlacement) {
                mc.gameSettings.keyBindJump.pressed = false
                event.setCancelled(true)
                val thePlayer: EntityPlayerSP = mc.thePlayer
                val thePlayer2: EntityPlayerSP = mc.thePlayer
                val n = 0.0
                thePlayer2.motionZ = n
                thePlayer.motionX = n
                mc.netHandler.addToSendQueue(
                    C0EPacketClickWindow(
                        mc.thePlayer.inventoryContainer.windowId,
                        36,
                        0,
                        2,
                        ItemStack(Block.getBlockById(166)),
                        0.toShort()
                    ) as Packet<*>
                )
            }
            if (event.packet is C07PacketPlayerDigging) {
                val thePlayer3: EntityPlayerSP = mc.thePlayer
                val thePlayer4: EntityPlayerSP = mc.thePlayer
                val n2 = 0.0
                thePlayer4.motionZ = n2
                thePlayer3.motionX = n2
            }
        }
        if (event.packet is C08PacketPlayerBlockPlacement && isHoldingPotionAndSword(
                mc.thePlayer.heldItem,
                false,
                false
            ) && mc.thePlayer.heldItem
                .item !is ItemSword && mc.thePlayer.heldItem != null && mc.thePlayer.heldItem
                .item != null && ((event.packet as C08PacketPlayerBlockPlacement).position.x != -1 || (event.packet as C08PacketPlayerBlockPlacement).position.y != -1 || (event.packet as C08PacketPlayerBlockPlacement).position.z != -1 && this.shouldCancelPlacement(
                mc.objectMouseOver
            ) )
        ) {
            event.setCancelled(true)
        }
    }

    private fun shouldCancelPlacement(objectPosition: MovingObjectPosition): Boolean {
        return !mutableListOf(54, 146, 61, 62).contains(
            Block.getIdFromBlock(
                mc.theWorld.getBlockState(objectPosition.blockPos).getBlock()
            )
        )
    }

    @EventTarget
    fun onMotion(event: MotionEvent?) {
        val heldItem = mc.thePlayer.heldItem
        if (heldItem == null || heldItem.item !is ItemSword || !MovementUtils.isMoving()) {
            return
        }
        val killAura = KillAura.instance
        if (!mc.thePlayer.isBlocking && !killAura!!.blockingStatus) {
            return
        }

        // laoshu
        if (mc.thePlayer.isBlocking && !mc.thePlayer.isEating && grim.get()) {
            mc.netHandler.networkManager.sendPacket(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN,
                    EnumFacing.DOWN
                )
            )
        }

        //lvziqiao
        if (mc.thePlayer.isBlocking && !mc.thePlayer.isEating && grim2.get()) {
            mc.netHandler.networkManager.sendPacket(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1))
            mc.netHandler.networkManager.sendPacket(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
        }

        //shenmiren
        if (grim3.get()) {
                if (event!!.state == EventState.PRE && (!mc.thePlayer!!.isUsingItem || mc.thePlayer.isBlocking) && this.isHoldingPotionAndSword(
                        mc.thePlayer.heldItem,
                        true,
                        false
                    )
                ) {
                    mc.netHandler!!.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
                }

                if (event.state == EventState.POST && (!mc.thePlayer!!.isUsingItem || mc.thePlayer.isBlocking) && this.isHoldingPotionAndSword(
                        mc.thePlayer.heldItem,
                        true,
                        false
                    )
                ) {
                    mc.netHandler.addToSendQueue(C0FPacketConfirmTransaction(Int.MAX_VALUE, 32767.toShort(), true))
                    mc.playerController.sendUseItem(mc.thePlayer!!, mc.theWorld!!, mc.thePlayer!!.heldItem!!)
            }
        }
    }

    private fun isHoldingPotionAndSword(stack: ItemStack?, checkSword: Boolean, checkPotionFood: Boolean): Boolean {
        if (stack == null) {
            return false
        }
        if (stack.item is ItemAppleGold && checkPotionFood) {
            return true
        }
        return if (stack.item is ItemPotion && checkPotionFood) {
            !ItemPotion.isSplash(stack.metadata)
        } else stack.item is ItemFood && checkPotionFood || stack.item is ItemSword && checkSword || stack.item !is ItemBow && stack.item is ItemBucketMilk && checkPotionFood
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        if (mc.thePlayer.heldItem == null) return
        val heldItem = mc.thePlayer.heldItem.item
        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }

    private fun getMultiplier(item: Item, isForward: Boolean): Float {
        return if (item is ItemFood || item is ItemPotion || item is ItemBucketMilk) {
            if (isForward) consumeForwardMultiplier.get() else consumeStrafeMultiplier.get()
        } else if (item is ItemSword) {
            if (isForward) blockForwardMultiplier.get() else blockStrafeMultiplier.get()
        } else if (item is ItemBow) {
            if (isForward) bowForwardMultiplier.get() else bowStrafeMultiplier.get()
        } else {
            0.2f
        }
    }

    companion object {
        @JvmStatic
        lateinit var instance: NoSlow
            private set
    }
}
