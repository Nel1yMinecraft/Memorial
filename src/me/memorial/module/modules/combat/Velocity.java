package me.memorial.module.modules.combat;

import me.memorial.Memorial;
import me.memorial.events.EventTarget;
import me.memorial.events.impl.move.JumpEvent;
import me.memorial.events.impl.misc.PacketEvent;
import me.memorial.events.impl.player.UpdateEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.module.modules.movement.Speed;
import me.memorial.utils.MovementUtils;
import me.memorial.utils.timer.MSTimer;
import me.memorial.value.BoolValue;
import me.memorial.value.FloatValue;
import me.memorial.value.ListValue;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MathHelper;

@ModuleInfo(name = "Velocity", description = "Allows you to modify the amount of knockback you take.", category = ModuleCategory.COMBAT)
public class Velocity extends Module {

    /**
     * OPTIONS
     */
    private final FloatValue horizontalValue = new FloatValue("Horizontal", 0F, 0F, 1F);
    private final FloatValue verticalValue = new FloatValue("Vertical", 0F, 0F, 1F);
    private final ListValue modeValue = new ListValue("Mode", new String[]{"Simple", "AAC", "AACPush", "AACZero",
            "Reverse", "SmoothReverse", "Jump", "Glitch"}, "Simple");

    // Reverse
    private final FloatValue reverseStrengthValue = new FloatValue("ReverseStrength", 1F, 0.1F, 1F);
    private final FloatValue reverse2StrengthValue = new FloatValue("SmoothReverseStrength", 0.05F, 0.02F, 0.1F);

    // AAC Push
    private final FloatValue aacPushXZReducerValue = new FloatValue("AACPushXZReducer", 2F, 1F, 3F);
    private final BoolValue aacPushYReducerValue = new BoolValue("AACPushYReducer", true);

    /**
     * VALUES
     */
    private final MSTimer velocityTimer = new MSTimer();
    private boolean velocityInput = false;

    // SmoothReverse
    private boolean reverseHurt = false;

    // AACPush
    private boolean jump = false;

    @Override
    public String getTag() {
        return modeValue.get();
    }

    @Override
    public void onDisable() {
        mc.thePlayer.speedInAir = 0.02F;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava() || mc.thePlayer.isInWeb)
            return;

        switch (modeValue.get().toLowerCase()) {
            case "jump":
                if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.onGround) {
                    mc.thePlayer.motionY = 0.42;

                    float yaw = mc.thePlayer.rotationYaw * 0.017453292F;
                    mc.thePlayer.motionX -= MathHelper.sin(yaw) * 0.2;
                    mc.thePlayer.motionZ += MathHelper.cos(yaw) * 0.2;
                }
                break;

            case "glitch":
                mc.thePlayer.noClip = velocityInput;
                if (mc.thePlayer.hurtTime == 7)
                    mc.thePlayer.motionY = 0.4;

                velocityInput = false;
                break;

            case "reverse":
                if (!velocityInput)
                    return;

                if (!mc.thePlayer.onGround) {
                    MovementUtils.strafe(MovementUtils.getSpeed() * reverseStrengthValue.get());
                } else if (velocityTimer.hasTimePassed(80L))
                    velocityInput = false;
                break;

            case "smoothreverse":
                if (!velocityInput) {
                    mc.thePlayer.speedInAir = 0.02F;
                    return;
                }

                if (mc.thePlayer.hurtTime > 0)
                    reverseHurt = true;

                if (!mc.thePlayer.onGround) {
                    if (reverseHurt)
                        mc.thePlayer.speedInAir = reverse2StrengthValue.get();
                } else if (velocityTimer.hasTimePassed(80L)) {
                    velocityInput = false;
                    reverseHurt = false;
                }
                break;

            case "aac":
                if (velocityInput && velocityTimer.hasTimePassed(80L)) {
                    mc.thePlayer.motionX *= horizontalValue.get();
                    mc.thePlayer.motionZ *= horizontalValue.get();
                    //mc.thePlayer.motionY *= verticalValue.get() ?
                    velocityInput = false;
                }
                break;

            case "aacpush":
                if (jump) {
                    if (mc.thePlayer.onGround)
                        jump = false;
                } else {
                    // Strafe
                    if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.motionX != 0.0 && mc.thePlayer.motionZ != 0.0)
                        mc.thePlayer.onGround = true;

                    // Reduce Y
                    if (mc.thePlayer.hurtResistantTime > 0 && aacPushYReducerValue.get()
                            && !Memorial.moduleManager.getModule(Speed.class).getState())
                        mc.thePlayer.motionY -= 0.014999993;
                }

                // Reduce XZ
                if (mc.thePlayer.hurtResistantTime >= 19) {
                    float reduce = aacPushXZReducerValue.get();

                    mc.thePlayer.motionX /= reduce;
                    mc.thePlayer.motionZ /= reduce;
                }
                break;

            case "aaczero":
                if (mc.thePlayer.hurtTime > 0) {
                    if (!velocityInput || mc.thePlayer.onGround || mc.thePlayer.fallDistance > 2F)
                        return;

                    mc.thePlayer.addVelocity(0.0, -1.0, 0.0);
                    mc.thePlayer.onGround = true;
                } else
                    velocityInput = false;
                break;
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (mc.thePlayer == null)
            return;

        Object packet = event.getPacket();

        if (packet instanceof S12PacketEntityVelocity) {
            if ((mc.theWorld != null ? mc.theWorld.getEntityByID(((S12PacketEntityVelocity) packet).getEntityID()) : null) != mc.thePlayer)
                return;

            velocityTimer.reset();

            switch (modeValue.get().toLowerCase()) {
                case "simple":
                    float horizontal = horizontalValue.get();
                    float vertical = verticalValue.get();

                    if (horizontal == 0F && vertical == 0F)
                        event.cancelEvent();

                    ((S12PacketEntityVelocity) packet).motionX = (int) (((S12PacketEntityVelocity) packet).getMotionX() * horizontal);
                    ((S12PacketEntityVelocity) packet).motionY = (int) (((S12PacketEntityVelocity) packet).getMotionY() * vertical);
                    ((S12PacketEntityVelocity) packet).motionZ = (int) (((S12PacketEntityVelocity) packet).getMotionZ() * horizontal);
                    break;

                case "aac":
                case "reverse":
                case "smoothreverse":
                case "aaczero":
                    velocityInput = true;
                    break;

                case "glitch":
                    if (!mc.thePlayer.onGround)
                        return;

                    velocityInput = true;
                    event.cancelEvent();
                    break;
            }
        }

        if (packet instanceof S27PacketExplosion) {
            // TODO: Support velocity for explosions
            event.cancelEvent();
        }
    }

    @EventTarget
    public void onJump(JumpEvent event) {
        if (mc.thePlayer == null || mc.thePlayer.isInWater() || mc.thePlayer.isInLava() || mc.thePlayer.isInWeb)
            return;

        switch (modeValue.get().toLowerCase()) {
            case "aacpush":
                jump = true;

                if (!mc.thePlayer.isCollidedVertically)
                    event.cancelEvent();
                break;

            case "aaczero":
                if (mc.thePlayer.hurtTime > 0)
                    event.cancelEvent();
                break;
        }
    }
}
