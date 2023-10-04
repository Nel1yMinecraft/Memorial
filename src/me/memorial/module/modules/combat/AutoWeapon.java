package me.memorial.module.modules.combat;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import com.google.common.collect.Multimap;
import org.lwjgl.input.Keyboard;
import me.memorial.events.EventTarget;
import me.memorial.events.impl.misc.PacketEvent;
import me.memorial.events.impl.player.AttackEvent;
import me.memorial.events.impl.player.UpdateEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.item.ItemUtils;
import me.memorial.value.BoolValue;
import me.memorial.value.IntegerValue;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

import java.util.Comparator;

@ModuleInfo(name = "AutoWeapon", description = "Automatically selects the best weapon in your hotbar.", category = ModuleCategory.COMBAT)
public class AutoWeapon extends Module {

    private final BoolValue silentValue = new BoolValue("SpoofItem", false);
    private final IntegerValue ticksValue = new IntegerValue("SpoofTicks", 10, 1, 20);

    private boolean attackEnemy = false;
    private int spoofedSlot = 0;

    @EventTarget
    public void onAttack(AttackEvent event) {
        attackEnemy = true;
    }

    @SuppressWarnings("unchecked")
    @EventTarget
    public void onPacket(PacketEvent event) {
        if (!(event.getPacket() instanceof C02PacketUseEntity)) {
            return;
        }

        C02PacketUseEntity packet = (C02PacketUseEntity) event.getPacket();
        if (packet.getAction() != C02PacketUseEntity.Action.ATTACK || !attackEnemy) {
            return;
        }

        attackEnemy = false;

        // Find best weapon in hotbar
        int bestSlot = -1;
        double highestDamage = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && (stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool)) {
                Multimap<String, AttributeModifier> attributeModifiers = stack.getAttributeModifiers();
                double damage = attributeModifiers.get(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName()).iterator().next().getAmount();
                damage += 1.25 * ItemUtils.getEnchantment(stack, Enchantment.sharpness);

                if (damage > highestDamage) {
                    bestSlot = i;
                    highestDamage = damage;
                }
            }
        }

        if (bestSlot == mc.thePlayer.inventory.currentItem) {
            return;
        }

        // Switch to best weapon
        if (silentValue.get()) {
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(bestSlot));
            spoofedSlot = ticksValue.get();
        } else {
            mc.thePlayer.inventory.currentItem = bestSlot;
            mc.playerController.updateController();
        }

        // Resend attack packet
        mc.getNetHandler().addToSendQueue(packet);
        event.setCancelled(true);
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        // Switch back to old item
        if (spoofedSlot > 0) {
            if (spoofedSlot == 1) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
            spoofedSlot--;
        }
    }
}
