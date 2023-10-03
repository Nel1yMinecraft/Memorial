package me.memorial.module.modules.world

import me.memorial.Memorial
import me.memorial.events.EventState
import me.memorial.events.EventTarget
import me.memorial.events.impl.move.MotionEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.module.modules.player.Blink
import me.memorial.utils.RotationUtils
import me.memorial.utils.block.BlockUtils
import me.memorial.utils.extensions.getVec
import me.memorial.utils.timer.MSTimer
import me.memorial.value.BlockValue
import me.memorial.value.BoolValue
import me.memorial.value.FloatValue
import me.memorial.value.IntegerValue
import net.minecraft.block.Block
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3

@ModuleInfo(name = "ChestAura", description = "Automatically opens chests around you.", category = ModuleCategory.WORLD)
object ChestAura : Module() {

    private val rangeValue = FloatValue("Range", 5F, 1F, 6F)
    private val delayValue = IntegerValue("Delay", 100, 50, 200)
    private val throughWallsValue = BoolValue("ThroughWalls", true)
    private val visualSwing = BoolValue("VisualSwing", true)
    private val chestValue = BlockValue("Chest", Block.getIdFromBlock(Blocks.chest))
    private val rotationsValue = BoolValue("Rotations", true)

    private var currentBlock: BlockPos? = null
    private val timer = MSTimer()

    val clickedBlocks = mutableListOf<BlockPos>()

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (Memorial.moduleManager[Blink::class.java]!!.state)
            return

        when (event.state) {
            EventState.PRE -> {
                if (mc.currentScreen is GuiContainer)
                    timer.reset()

                val radius = rangeValue.get() + 1

                val eyesPos = Vec3(mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight(),
                        mc.thePlayer.posZ)

                currentBlock = BlockUtils.searchBlocks(radius.toInt())
                        .filter {
                            Block.getIdFromBlock(it.value) == chestValue.get() && !clickedBlocks.contains(it.key)
                                    && BlockUtils.getCenterDistance(it.key) < rangeValue.get()
                        }
                        .filter {
                            if (throughWallsValue.get())
                                return@filter true

                            val blockPos = it.key
                            val movingObjectPosition = mc.theWorld.rayTraceBlocks(eyesPos,
                                    blockPos.getVec(), false, true, false)

                            movingObjectPosition != null && movingObjectPosition.blockPos == blockPos
                        }
                        .minByOrNull { BlockUtils.getCenterDistance(it.key) }?.key

                if (rotationsValue.get())
                    RotationUtils.setTargetRotation((RotationUtils.faceBlock(currentBlock ?: return)
                            ?: return).rotation)
            }

            EventState.POST -> if (currentBlock != null && timer.hasTimePassed(delayValue.get().toLong())) {
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem, currentBlock,
                                EnumFacing.DOWN, currentBlock!!.getVec())) {
                    if (visualSwing.get())
                        mc.thePlayer.swingItem()
                    else
                        mc.netHandler.addToSendQueue(C0APacketAnimation())

                    clickedBlocks.add(currentBlock!!)
                    currentBlock = null
                    timer.reset()
                }
            }
        }
    }

    override fun onDisable() {
        clickedBlocks.clear()
    }
}