package me.memorial.module.modules.movement;

import me.memorial.events.*;

import me.memorial.events.impl.misc.PacketEvent;
import me.memorial.events.impl.move.JumpEvent;
import me.memorial.events.impl.move.MotionEvent;
import me.memorial.events.impl.move.MoveEvent;
import me.memorial.events.impl.move.StepEvent;
import me.memorial.events.impl.player.UpdateEvent;
import me.memorial.events.impl.render.Render3DEvent;
import me.memorial.events.impl.world.BlockBBEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.ClientUtils;
import me.memorial.utils.MovementUtils;
import me.memorial.utils.render.RenderUtils;
import me.memorial.utils.timer.MSTimer;
import me.memorial.utils.timer.TickTimer;
import me.memorial.value.BoolValue;
import me.memorial.value.FloatValue;
import me.memorial.value.IntegerValue;
import me.memorial.value.ListValue;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@ModuleInfo(name = "Fly", description = "Allows you to fly in survival mode.", category = ModuleCategory.MOVEMENT, keyBind = Keyboard.KEY_F)
public class Fly extends Module {

    public final ListValue modeValue = new ListValue("Mode", new String[]{
            "Vanilla",
            "SmoothVanilla",
    }, "Vanilla");

    private final FloatValue vanillaSpeedValue = new FloatValue("VanillaSpeed", 2F, 0F, 5F);
    private final BoolValue vanillaKickBypassValue = new BoolValue("VanillaKickBypass", false);

    private final BoolValue dragon = new BoolValue("Dragon", true);

    // Visuals
    private final BoolValue markValue = new BoolValue("Mark", true);

    private final MSTimer groundTimer = new MSTimer();
    private final TickTimer hypixelTimer = new TickTimer();

    private boolean failedStart = false;
    private EntityDragon entityDragon;

    @Override
    public void onDisable() {
        if (entityDragon != null) {
            mc.theWorld.removeEntity(entityDragon);
            entityDragon = null;
        }

        if (mc.thePlayer == null)
            return;

        mc.thePlayer.capabilities.isFlying = false;

        mc.timer.timerSpeed = 1F;
        mc.thePlayer.speedInAir = 0.02F;
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {

        if (dragon.get()) {
            if (entityDragon == null) {
                entityDragon = new EntityDragon(mc.theWorld);
                mc.theWorld.addEntityToWorld(-1, entityDragon);
            }

            final Vector3d position = new Vector3d(
                    mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks,
                    mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks,
                    mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks
            );

            entityDragon.setPositionAndRotation(position.x, position.y - 3, position.z,
                    mc.thePlayer.rotationYaw - 180, mc.thePlayer.rotationPitch);
        }
        final float vanillaSpeed = vanillaSpeedValue.get();

        switch (modeValue.get().toLowerCase()) {
            case "vanilla":
                mc.thePlayer.capabilities.isFlying = false;
                mc.thePlayer.motionY = 0;
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
                if (mc.gameSettings.keyBindJump.isKeyDown())
                    mc.thePlayer.motionY += vanillaSpeed;
                if (mc.gameSettings.keyBindSneak.isKeyDown())
                    mc.thePlayer.motionY -= vanillaSpeed;
                MovementUtils.strafe(vanillaSpeed);

                handleVanillaKickBypass();
                break;
            case "smoothvanilla":
                mc.thePlayer.capabilities.isFlying = true;

                handleVanillaKickBypass();
                break;
        }
    }


    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        final String mode = modeValue.get();

        if (!markValue.get() || mode.equalsIgnoreCase("Vanilla") || mode.equalsIgnoreCase("SmoothVanilla"))
            return;

        double y = 2D;

        RenderUtils.drawPlatform(y, mc.thePlayer.getEntityBoundingBox().maxY < y ? new Color(0, 255, 0, 90) : new Color(255, 0, 0, 90), 1);

    }

    private void handleVanillaKickBypass() {
        if(!vanillaKickBypassValue.get() || !groundTimer.hasTimePassed(1000)) return;

        final double ground = calculateGround();

        for(double posY = mc.thePlayer.posY; posY > ground; posY -= 8D) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true));

            if(posY - 8D < ground) break; // Prevent next step
        }

        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, ground, mc.thePlayer.posZ, true));


        for(double posY = ground; posY < mc.thePlayer.posY; posY += 8D) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true));

            if(posY + 8D > mc.thePlayer.posY) break; // Prevent next step
        }

        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));

        groundTimer.reset();
    }

    // TODO: Make better and faster calculation lol
    private double calculateGround() {
        final AxisAlignedBB playerBoundingBox = mc.thePlayer.getEntityBoundingBox();
        double blockHeight = 1D;

        for(double ground = mc.thePlayer.posY; ground > 0D; ground -= blockHeight) {
            final AxisAlignedBB customBox = new AxisAlignedBB(playerBoundingBox.maxX, ground + blockHeight, playerBoundingBox.maxZ, playerBoundingBox.minX, ground, playerBoundingBox.minZ);

            if(mc.theWorld.checkBlockCollision(customBox)) {
                if(blockHeight <= 0.05D)
                    return ground + blockHeight;

                ground += blockHeight;
                blockHeight = 0.05D;
            }
        }

        return 0F;
    }

    @Override
    public String getTag() {
        return modeValue.get();
    }
}
