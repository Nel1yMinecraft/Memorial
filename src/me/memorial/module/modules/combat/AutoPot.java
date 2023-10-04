package me.memorial.module.modules.combat;

import me.memorial.events.EventState;
import me.memorial.events.EventTarget;
import me.memorial.events.impl.move.MotionEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.InventoryUtils;
import me.memorial.utils.Rotation;
import me.memorial.utils.RotationUtils;
import me.memorial.utils.misc.FallingPlayer;
import me.memorial.utils.misc.RandomUtils;
import me.memorial.utils.timer.MSTimer;
import me.memorial.value.BoolValue;
import me.memorial.value.FloatValue;
import me.memorial.value.IntegerValue;
import me.memorial.value.ListValue;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@ModuleInfo(name = "AutoPot", description = "Automatically throws healing potions.", category = ModuleCategory.COMBAT)
public class AutoPot extends Module {

    private FloatValue healthValue = new FloatValue("Health", 15F, 1F, 20F);
    private IntegerValue delayValue = new IntegerValue("Delay", 500, 500, 1000);

    private BoolValue openInventoryValue = new BoolValue("OpenInv", false);
    private BoolValue simulateInventory = new BoolValue("SimulateInventory", true);

    private FloatValue groundDistanceValue = new FloatValue("GroundDistance", 2F, 0F, 5F);
    private ListValue modeValue = new ListValue("Mode", new String[]{"Normal", "Jump", "Port"}, "Normal");

    private MSTimer msTimer = new MSTimer();
    private int potion = -1;

    @EventTarget
    public void onMotion(MotionEvent motionEvent) {
        if (!msTimer.hasTimePassed((long) delayValue.get()) || mc.playerController.isInCreativeMode()) {
            return;
        }

        switch (motionEvent.getState()) {
            case PRE:
                // Hotbar Potion
                int potionInHotbar = findPotion(36, 45);

                if (mc.thePlayer.getHealth() <= healthValue.get() && potionInHotbar != -1) {
                    if (mc.thePlayer.onGround) {
                        switch (modeValue.get().toLowerCase()) {
                            case "jump":
                                mc.thePlayer.jump();
                                break;
                            case "port":
                                mc.thePlayer.moveEntity(0.0, 0.42, 0.0);
                                break;
                        }
                    }

                    // Prevent throwing potions into the void
                    FallingPlayer fallingPlayer = new FallingPlayer(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            mc.thePlayer.motionX,
                            mc.thePlayer.motionY,
                            mc.thePlayer.motionZ,
                            mc.thePlayer.rotationYaw,
                            mc.thePlayer.moveStrafing,
                            mc.thePlayer.moveForward
                    );

                    net.minecraft.util.BlockPos collisionBlock = fallingPlayer.findCollision(20).getPos();

                    if (mc.thePlayer.posY - (collisionBlock != null ? collisionBlock.getY() : 0) >= groundDistanceValue.get()) {
                        return;
                    }

                    potion = potionInHotbar;
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(potion - 36));

                    if (mc.thePlayer.rotationPitch <= 80F) {
                        RotationUtils.setTargetRotation(new Rotation(mc.thePlayer.rotationYaw, RandomUtils.nextFloat(80F, 90F)));
                    }
                    return;
                }

                // Inventory Potion -> Hotbar Potion
                int potionInInventory = findPotion(9, 36);
                if (potionInInventory != -1 && InventoryUtils.hasSpaceHotbar()) {
                    if (openInventoryValue.get() && mc.currentScreen instanceof GuiInventory) {
                        return;
                    }

                    boolean openInventory = mc.currentScreen instanceof GuiInventory && simulateInventory.get();

                    if (openInventory) {
                        mc.getNetHandler().addToSendQueue(
                                new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    }

                    mc.playerController.windowClick(0, potionInInventory, 0, 1, mc.thePlayer);

                    if (openInventory) {
                        mc.getNetHandler().addToSendQueue(new C0DPacketCloseWindow());
                    }

                    msTimer.reset();
                }
                break;
            case POST:
                if (potion >= 0 && RotationUtils.serverRotation.getPitch() >= 75F) {
                    net.minecraft.item.ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(potion).getStack();

                    if (itemStack != null) {
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(itemStack));
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));

                        msTimer.reset();
                    }

                    potion = -1;
                }
                break;
        }
    }

    private int findPotion(int startSlot, int endSlot) {
        for (int i = startSlot; i < endSlot; i++) {
            net.minecraft.item.ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (stack == null || stack.getItem() != Items.potionitem || !ItemPotion.isSplash(stack.getItemDamage())) {
                continue;
            }

            ItemPotion itemPotion = (ItemPotion) stack.getItem();

            for (PotionEffect potionEffect : itemPotion.getEffects(stack)) {
                if (potionEffect.getPotionID() == Potion.heal.getId()) {
                    return i;
                }
            }

            if (!mc.thePlayer.isPotionActive(Potion.regeneration)) {
                for (PotionEffect potionEffect : itemPotion.getEffects(stack)) {
                    if (potionEffect.getPotionID() == Potion.regeneration.getId()) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    @Override
    public String getTag() {
        return String.valueOf(healthValue.get());
    }
}