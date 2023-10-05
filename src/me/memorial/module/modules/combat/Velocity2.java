package me.memorial.module.modules.combat;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import me.memorial.events.EventTarget;
import me.memorial.events.impl.misc.PacketEvent;
import me.memorial.events.impl.misc.TickEvent;
import me.memorial.events.impl.move.MotionEvent;
import me.memorial.events.impl.move.StrafeEvent;
import me.memorial.events.impl.player.UpdateEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.MinecraftInstance;
import me.memorial.value.BoolValue;
import me.memorial.value.FloatValue;
import me.memorial.value.IntegerValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.NotNull;

@ModuleInfo(
        name = "Velocity2",
        description = "By Kid .",
        category = ModuleCategory.COMBAT
)

public final class Velocity2 extends Module {
    private final IntegerValue airticks = new IntegerValue("AirTicks", 2, 0, 10);
    private final FloatValue timerSpeed = new FloatValue("TimerSpeed", 0.49F, 0.1F, 1.0F);
    private final BoolValue airTest = new BoolValue("AirTest", true);
    private int airtick;
    private boolean air;
    private boolean pre;

    public void onEnable() {
        this.airtick = 0;
        this.air = false;
        this.air = false;
        this.pre = false;
    }

    public void onDisable() {
        MinecraftInstance.mc.timer.timerSpeed = 1.0F;
    }

    @EventTarget
    public final void onPacket(@NotNull PacketEvent event) {
        Intrinsics.checkNotNullParameter(event, "event");
        Packet packet = event.getPacket();
        if (packet instanceof S12PacketEntityVelocity) {
            int var10000 = ((S12PacketEntityVelocity)packet).getEntityID();
            EntityPlayerSP var10001 = MinecraftInstance.mc.thePlayer;
            Intrinsics.checkNotNullExpressionValue(var10001, "mc.thePlayer");
            if (var10000 == var10001.getEntityId() && (Boolean)this.airTest.get()) {
                event.cancelEvent();
                this.airtick = ((Number)this.airticks.get()).intValue();
            }
        }

        if (this.airtick > 0) {
            MinecraftInstance.mc.timer.timerSpeed = ((Number)this.timerSpeed.get()).floatValue();
            if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition || packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
                Minecraft var3 = MinecraftInstance.mc;
                Intrinsics.checkNotNullExpressionValue(var3, "mc");
                var3.getNetHandler().addToSendQueue((Packet)(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, new BlockPos(MinecraftInstance.mc.thePlayer.posX, MinecraftInstance.mc.thePlayer.posY, MinecraftInstance.mc.thePlayer.posZ), EnumFacing.UP)));
                var3 = MinecraftInstance.mc;
                Intrinsics.checkNotNullExpressionValue(var3, "mc");
                var3.getNetHandler().addToSendQueue((Packet)(new C03PacketPlayer(MinecraftInstance.mc.thePlayer.onGround)));
                var3 = MinecraftInstance.mc;
                Intrinsics.checkNotNullExpressionValue(var3, "mc");
                var3.getNetHandler().addToSendQueue((Packet)(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, new BlockPos(MinecraftInstance.mc.thePlayer.posX, MinecraftInstance.mc.thePlayer.posY + (double)1, MinecraftInstance.mc.thePlayer.posZ), EnumFacing.UP)));
            }
        }

    }

    @EventTarget
    public final void onUpdate(@NotNull UpdateEvent event) {
        Intrinsics.checkNotNullParameter(event, "event");
        if (this.airtick > 0) {
            this.air = true;
            this.airtick += -1;
        }

        if (this.air && this.airtick < 1) {
            MinecraftInstance.mc.timer.timerSpeed = 1.0F;
            this.air = false;
        }

    }

    @EventTarget
    public final void onTIck(@NotNull TickEvent event) {
        Intrinsics.checkNotNullParameter(event, "event");
        if (this.airtick > 0) {
            Minecraft var10000 = MinecraftInstance.mc;
            Intrinsics.checkNotNullExpressionValue(var10000, "mc");
            var10000.getNetHandler().addToSendQueue((Packet)(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, new BlockPos(MinecraftInstance.mc.thePlayer.posX, MinecraftInstance.mc.thePlayer.posY, MinecraftInstance.mc.thePlayer.posZ), EnumFacing.UP)));
        }

    }

    @EventTarget
    public final void onStrafe(@NotNull StrafeEvent event) {
        Intrinsics.checkNotNullParameter(event, "event");
    }

    @EventTarget
    public final void onMotion(@NotNull MotionEvent event) {
        Intrinsics.checkNotNullParameter(event, "event");
        this.pre = !StringsKt.equals(event.getState().getStateName(), "Post", true);
    }
}
