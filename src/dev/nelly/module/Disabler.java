package dev.nelly.module;


import me.memorial.events.EventTarget;
import me.memorial.events.impl.misc.PacketEvent;
import me.memorial.events.impl.player.UpdateEvent;
import me.memorial.events.impl.world.WorldEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.timer.TimeUtils;
import me.memorial.value.BoolValue;
import net.minecraft.network.*;
import java.util.*;
import net.minecraft.network.play.client.*;
import org.jetbrains.annotations.Nullable;
@ModuleInfo(name ="Disabler",description =  "1", category =ModuleCategory.CLIENT)
public class Disabler extends Module
{
    BoolValue postValue;
    BoolValue badPacketsA;
    BoolValue badPacketsF;
    BoolValue fakePingValue;
    private final HashMap<Packet<?>, Long> packetsMap;
    int lastSlot;
    boolean startChiJiDisabler;
    boolean lastSprinting;
    
    public Disabler() {
        this.postValue = new BoolValue("Grim-Post", Boolean.valueOf(false));
        this.badPacketsA = new BoolValue("Grim-BadPacketsA", Boolean.valueOf(false));
        this.badPacketsF = new BoolValue("Grim-BadPacketsF", Boolean.valueOf(false));
        this.fakePingValue = new BoolValue("Grim-FakePing", Boolean.valueOf(false));
        this.packetsMap = new HashMap<>();
        this.lastSlot = -1;
    }
    


    @Nullable
    @Override
    public String getTag() {
        return "Grim";
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if (this.fakePingValue.get()) {
            try {
                synchronized (this.packetsMap) {
                    final Iterator<Map.Entry<Packet<?>, Long>> iterator = this.packetsMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        final Map.Entry<Packet<?>, Long> entry = iterator.next();
                        if (entry.getValue() < System.currentTimeMillis()) {
                            Disabler.mc.getNetHandler().addToSendQueue((Packet)entry.getKey());
                            iterator.remove();
                        }
                    }
                }
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
    
    @EventTarget
    public void onWorld(final WorldEvent event) {
        this.lastSlot = -1;
        this.lastSprinting = false;
        this.startChiJiDisabler = false;
    }
    
    @EventTarget
    public void onPacket(final PacketEvent event) {
        final Packet<?> packet = (Packet<?>)event.getPacket();

        if (Disabler.mc.thePlayer == null) {
            return;
        }
        if (Disabler.mc.thePlayer.isDead) {
            return;
        }
        if (this.badPacketsF.get() && packet instanceof C0BPacketEntityAction) {
            if (((C0BPacketEntityAction)packet).getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                if (this.lastSprinting) {
                    event.setCancelled(true);
                }
                this.lastSprinting = true;
            }
            else if (((C0BPacketEntityAction)packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                if (!this.lastSprinting) {
                    event.setCancelled(true);
                }
                this.lastSprinting = false;
            }
        }
        if (this.badPacketsA.get() && packet instanceof C09PacketHeldItemChange) {
            final int slot = ((C09PacketHeldItemChange)packet).getSlotId();
            if (slot == this.lastSlot && slot != -1) {
                event.setCancelled(true);
            }
            this.lastSlot = ((C09PacketHeldItemChange)packet).getSlotId();
        }
        if (this.fakePingValue.get() && (packet instanceof C00PacketKeepAlive || packet instanceof C16PacketClientStatus) && Disabler.mc.thePlayer.getHealth() > 0.0f && !this.packetsMap.containsKey(packet)) {
            event.setCancelled(true);
            synchronized (this.packetsMap) {
                this.packetsMap.put(packet, System.currentTimeMillis() + TimeUtils.randomDelay(199999, 9999999));
            }
        }
        if (this.postValue.get()) {
            if (packet instanceof C0APacketAnimation) {
                sendPacketC0F();
            }
            if (packet instanceof C13PacketPlayerAbilities) {
                sendPacketC0F();
            }
            if (packet instanceof C08PacketPlayerBlockPlacement) {
                sendPacketC0F();
            }
            if (packet instanceof C07PacketPlayerDigging) {
                sendPacketC0F();
            }
            if (packet instanceof C02PacketUseEntity) {
                sendPacketC0F();
            }
            if (packet instanceof C0EPacketClickWindow) {
                sendPacketC0F();
            }
            if (packet instanceof C0BPacketEntityAction) {
                sendPacketC0F();
            }
        }
    }
    public static void sendPacketC0F() {
        mc.getNetHandler().getNetworkManager().sendPacket(new C0FPacketConfirmTransaction(Integer.MAX_VALUE, (short)32767, true));
    }

}
