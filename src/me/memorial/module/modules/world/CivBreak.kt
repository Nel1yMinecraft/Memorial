package me.memorial.module.modules.world

import me.memorial.events.*
import me.memorial.events.impl.player.MotionEvent
import me.memorial.events.impl.render.Render3DEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.utils.RotationUtils
import me.memorial.utils.block.BlockUtils
import me.memorial.utils.render.RenderUtils
import me.memorial.value.BoolValue
import me.memorial.value.FloatValue
import net.minecraft.block.BlockAir
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.awt.Color

@ModuleInfo(name = "CivBreak", description = "Allows you to break blocks instantly.", category = ModuleCategory.WORLD)
class CivBreak : Module() {

    private var blockPos: BlockPos? = null
    private var enumFacing: EnumFacing? = null

    private val range = FloatValue("Range", 5F, 1F, 6F)
    private val rotationsValue = BoolValue("Rotations", true)
    private val visualSwingValue = BoolValue("VisualSwing", true)

    private val airResetValue = BoolValue("Air-Reset", true)
    private val rangeResetValue = BoolValue("Range-Reset", true)


    @EventTarget
    fun onBlockClick(event: ClickBlockEvent) {
        if (BlockUtils.getBlock(event.clickedBlock) == Blocks.bedrock)
            return

        blockPos = event.clickedBlock
        enumFacing = event.enumFacing

        // Break
        mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, enumFacing))
        mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, enumFacing))
    }

    @EventTarget
    fun onUpdate(event: MotionEvent) {
        val pos = blockPos ?: return

        if (airResetValue.get() && BlockUtils.getBlock(pos) is BlockAir ||
                rangeResetValue.get() && BlockUtils.getCenterDistance(pos) > range.get()) {
            blockPos = null
            return
        }

        if (BlockUtils.getBlock(pos) is BlockAir || BlockUtils.getCenterDistance(pos) > range.get())
            return

        when (event.state) {
            EventState.PRE -> if (rotationsValue.get())
                RotationUtils.setTargetRotation((RotationUtils.faceBlock(pos) ?: return).rotation)

            EventState.POST -> {
                if (visualSwingValue.get())
                    mc.thePlayer.swingItem()
                else
                    mc.netHandler.addToSendQueue(C0APacketAnimation())

                // Break
                mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                        blockPos, enumFacing))
                mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                        blockPos, enumFacing))
                mc.playerController.clickBlock(blockPos, enumFacing)
            }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        RenderUtils.drawBlockBox(blockPos ?: return, Color.RED, true)
    }
}