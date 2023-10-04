package me.memorial.module.modules.world;

import me.memorial.Memorial;
import me.memorial.events.EventTarget;
import me.memorial.events.impl.misc.PacketEvent;
import me.memorial.events.impl.render.Render3DEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.module.modules.player.InventoryCleaner;
import me.memorial.utils.timer.MSTimer;
import me.memorial.utils.timer.TimeUtils;
import me.memorial.value.BoolValue;
import me.memorial.value.IntegerValue;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ModuleInfo(name = "ChestStealer", description = "Automatically steals all items from a chest.", category = ModuleCategory.WORLD)
public class ChestStealer extends Module {

    /**
     * OPTIONS
     */

    private final IntegerValue maxDelayValue = new IntegerValue("MaxDelay", 200, 0, 400);

    private final IntegerValue minDelayValue = new IntegerValue("MinDelay", 150, 0, 400);

    private final BoolValue takeRandomizedValue = new BoolValue("TakeRandomized", false);
    private final BoolValue onlyItemsValue = new BoolValue("OnlyItems", false);
    private final BoolValue noCompassValue = new BoolValue("NoCompass", false);
    private final BoolValue autoCloseValue = new BoolValue("AutoClose", true);

    private final IntegerValue autoCloseMaxDelayValue = new IntegerValue("AutoCloseMaxDelay", 0, 0, 400);

    private final IntegerValue autoCloseMinDelayValue = new IntegerValue("AutoCloseMinDelay", 0, 0, 400) ;

    private final BoolValue closeOnFullValue = new BoolValue("CloseOnFull", true);
    private final BoolValue chestTitleValue = new BoolValue("ChestTitle", false);

    /**
     * VALUES
     */

    private final MSTimer delayTimer = new MSTimer();
    private int nextDelay = (int) TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get());

    private final MSTimer autoCloseTimer = new MSTimer();
    private int nextCloseDelay = (int) TimeUtils.randomDelay(autoCloseMinDelayValue.get(), autoCloseMaxDelayValue.get());

    private int contentReceived = 0;

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        GuiChest screen = (GuiChest) mc.currentScreen;

        if (screen instanceof GuiChest && delayTimer.hasTimePassed(nextDelay)) {
            autoCloseTimer.reset();
        } else {
            return;
        }

        // No Compass
        if (noCompassValue.get() && mc.thePlayer.inventory.getCurrentItem().getItem().getUnlocalizedName().equals("item.compass")) {
            return;
        }

        // Chest title
        if (chestTitleValue.get() && (screen.lowerChestInventory == null || !screen.lowerChestInventory.getName().contains(new ItemStack(Item.itemRegistry.getObject(new ResourceLocation("minecraft:chest"))).getDisplayName()))) {
            return;
        }

        // inventory cleaner
        InventoryCleaner inventoryCleaner = (InventoryCleaner) Memorial.moduleManager.getModule(InventoryCleaner.class);

        // Is empty?
        if (!isEmpty(screen) && !(closeOnFullValue.get() && isFullInventory())) {
            autoCloseTimer.reset();

            // Randomized
            if (takeRandomizedValue.get()) {
                List<Slot> items = new ArrayList<>();

                for (int slotIndex = 0; slotIndex < screen.inventoryRows * 9; slotIndex++) {
                    Slot slot = screen.inventorySlots.inventorySlots.get(slotIndex);

                    if (slot.getStack() != null && (!onlyItemsValue.get() || !(slot.getStack().getItem() instanceof ItemBlock)) && (!inventoryCleaner.getState() || inventoryCleaner.isUseful(slot.getStack(), -1))) {
                        items.add(slot);
                    }
                }

                while (delayTimer.hasTimePassed(nextDelay) && !items.isEmpty()) {
                    int randomSlot = new Random().nextInt(items.size());
                    Slot slot = items.get(randomSlot);

                    move(screen, slot);

                    items.remove(slot);
                }

                return;
            }

            // Non randomized
            for (int slotIndex = 0; slotIndex < screen.inventoryRows * 9; slotIndex++) {
                Slot slot = screen.inventorySlots.inventorySlots.get(slotIndex);

                if (delayTimer.hasTimePassed(nextDelay) && slot.getStack() != null &&
                        (!onlyItemsValue.get() || !(slot.getStack().getItem() instanceof ItemBlock)) && (!inventoryCleaner.getState() || inventoryCleaner.isUseful(slot.getStack(), -1))) {
                    move(screen, slot);
                }
            }
        } else if (autoCloseValue.get() && screen.inventorySlots.windowId == contentReceived && autoCloseTimer.hasTimePassed(nextCloseDelay)) {
            mc.thePlayer.closeScreen();
            nextCloseDelay = (int) TimeUtils.randomDelay(autoCloseMinDelayValue.get(), autoCloseMaxDelayValue.get());
        }
    }

    @EventTarget
    private void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S30PacketWindowItems) {
            S30PacketWindowItems packet = (S30PacketWindowItems) event.getPacket();
            contentReceived = packet.func_148911_c();
        }
    }

    private void move(GuiChest screen, Slot slot) {
        screen.handleMouseClick(slot, slot.slotNumber, 0, 1);
        delayTimer.reset();
        nextDelay = (int) TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get());
    }

    private boolean isEmpty(GuiChest chest) {
        InventoryCleaner inventoryCleaner = (InventoryCleaner) Memorial.moduleManager.getModule(InventoryCleaner.class);

        for (int i = 0; i < chest.inventoryRows * 9; i++) {
            Slot slot = chest.inventorySlots.inventorySlots.get(i);

            if (slot.getStack() != null && (!onlyItemsValue.get() || !(slot.getStack().getItem() instanceof ItemBlock)) && (!inventoryCleaner.getState() || inventoryCleaner.isUseful(slot.getStack(), -1))) {
                return false;
            }
        }

        return true;
    }

    private boolean isFullInventory() {
        for (ItemStack itemStack : mc.thePlayer.inventory.mainInventory) {
            if (itemStack == null) {
                return false;
            }
        }

        return true;
    }
}
