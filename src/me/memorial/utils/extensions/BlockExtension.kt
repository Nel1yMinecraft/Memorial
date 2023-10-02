package me.memorial.utils.extensions

import me.memorial.utils.block.BlockUtils
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3

/**
 * Get block by position
 */
fun BlockPos.getBlock() = BlockUtils.getBlock(this)

/**
 * Get vector of block position
 */
fun BlockPos.getVec() = Vec3(x + 0.5, y + 0.5, z + 0.5)