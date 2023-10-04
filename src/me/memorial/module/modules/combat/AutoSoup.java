package me.memorial.module.modules.combat;

import me.memorial.events.EventTarget;
import me.memorial.events.impl.player.UpdateEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.InventoryUtils;
import me.memorial.utils.timer.MSTimer;
import me.memorial.value.BoolValue;
import me.memorial.value.FloatValue;
import me.memorial.value.IntegerValue;
import me.memorial.value.ListValue;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(name = "AutoSoup", description = "Makes you automatically eat soup whenever your health is low.", category = ModuleCategory.COMBAT)
public class AutoSoup extends Module {

    private final FloatValue healthValue = new FloatValue("Health", 15f, 0f, 20f);
    private final IntegerValue delayValue = new IntegerValue("Delay", 150, 0, 500);
    private final BoolValue openInventoryValue = new BoolValue("OpenInv", false);
    private final BoolValue simulateInventoryValue = new BoolValue("SimulateInventory", true);
    private final ListValue bowlValue = new ListValue("Bowl", new String[]{"Drop", "Move", "Stay"}, "Drop");

    private final MSTimer timer = new MSTimer();

    @Override
    public String getTag() {
        return String.valueOf(healthValue.get());
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (!timer.hasTimePassed(delayValue.get())) {
            return;
        }

        int soupInHotbar = InventoryUtils.findItem(36, 45, Items.mushroom_stew);
        if (mc.thePlayer.getHealth() <= healthValue.get() && soupInHotbar != -1) {
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(soupInHotbar - 36));
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer
                    .getSlot(soupInHotbar).getStack()));
            if (bowlValue.get().equalsIgnoreCase("Drop")) {
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM,
                        BlockPos.ORIGIN, EnumFacing.DOWN));
            }
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            timer.reset();
            return;
        }

        int bowlInHotbar = InventoryUtils.findItem(36, 45, Items.bowl);
        if (bowlValue.get().equalsIgnoreCase("Move") && bowlInHotbar != -1) {
            if (openInventoryValue.get() && !(mc.currentScreen instanceof GuiInventory)) {
                return;
            }

            boolean bowlMovable = false;

            for (int i = 9; i <= 36; i++) {
                net.minecraft.item.ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

                if (itemStack == null) {
                    bowlMovable = true;
                    break;
                } else if (itemStack.getItem() == Items.bowl && itemStack.stackSize < 64) {
                    bowlMovable = true;
                    break;
                }
            }

            if (bowlMovable) {
                boolean openInventory = mc.currentScreen instanceof GuiInventory && simulateInventoryValue.get();

                if (openInventory) {
                    mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                }
                mc.playerController.windowClick(0, bowlInHotbar, 0, 1, mc.thePlayer);
            }
        }

        int soupInInventory = InventoryUtils.findItem(9, 36, Items.mushroom_stew);
        if (soupInInventory != -1 && InventoryUtils.hasSpaceHotbar()) {
            if (openInventoryValue.get() && !(mc.currentScreen instanceof GuiInventory)) {
                return;
            }

            boolean openInventory = mc.currentScreen instanceof GuiInventory && simulateInventoryValue.get();
            if (openInventory) {
                mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            }

            mc.playerController.windowClick(0, soupInInventory, 0, 1, mc.thePlayer);

            if (openInventory) {
                mc.getNetHandler().addToSendQueue(new C0DPacketCloseWindow());
            }

            timer.reset();
        }
    }
}
