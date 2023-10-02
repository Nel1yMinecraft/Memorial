package me.memorial.script.api.global

import me.memorial.utils.item.ItemUtils
import net.minecraft.item.ItemStack

/**
 * Object used by the script API to provide an easier way of creating items.
 */
object Item {

    /**
     * Creates an item.
     * @param itemArguments Arguments describing the item.
     * @return An instance of [ItemStack] with the given data.
     */
    @JvmStatic
    fun create(itemArguments: String): ItemStack = ItemUtils.createItem(itemArguments)
}