package me.memorial.module.modules.combat;

import me.memorial.events.EventTarget;
import me.memorial.events.impl.player.UpdateEvent;
import me.memorial.events.impl.render.Render3DEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.misc.RandomUtils;
import me.memorial.utils.timer.TimeUtils;
import me.memorial.value.BoolValue;
import me.memorial.value.IntegerValue;
import net.minecraft.client.settings.KeyBinding;
import kotlin.random.Random;

import static net.optifine.CustomColors.random;

@ModuleInfo(name = "AutoClicker", description = "Constantly clicks when holding down a mouse button.", category = ModuleCategory.COMBAT)
public class AutoClicker extends Module {
    private static AutoClicker instance;

    public AutoClicker() {
        instance = this;
    }

    private IntegerValue maxCPSValue = new IntegerValue("MaxCPS", 8, 1, 20) {

        public void onChanged(int oldValue, int newValue) {
            int minCPS = minCPSValue.get();
            if (minCPS > newValue) {
                set(minCPS);
            }
        }
    };

    private IntegerValue minCPSValue = new IntegerValue("MinCPS", 5, 1, 20) {
        public void onChanged(int oldValue, int newValue) {
            int maxCPS = maxCPSValue.get();
            if (maxCPS < newValue) {
                set(maxCPS);
            }
        }
    };

    private BoolValue rightValue = new BoolValue("Right", true);
    private BoolValue leftValue = new BoolValue("Left", true);
    private BoolValue jitterValue = new BoolValue("Jitter", false);

    private long rightDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get());
    private long rightLastSwing = 0L;
    private long leftDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get());
    private long leftLastSwing = 0L;

    @EventTarget
    public void onRender(Render3DEvent event) {
        // Left click
        if (mc.gameSettings.keyBindAttack.isKeyDown() && leftValue.get()
                && System.currentTimeMillis() - leftLastSwing >= leftDelay && mc.playerController.curBlockDamageMP == 0F) {
            KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode()); // Minecraft Click Handling

            leftLastSwing = System.currentTimeMillis();
            leftDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get());
        }

        // Right click
        if (mc.gameSettings.keyBindUseItem.isKeyDown() && !mc.thePlayer.isUsingItem() && rightValue.get()
                && System.currentTimeMillis() - rightLastSwing >= rightDelay) {
            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode()); // Minecraft Click Handling

            rightLastSwing = System.currentTimeMillis();
            rightDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get());
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (jitterValue.get() && (leftValue.get() && mc.gameSettings.keyBindAttack.isKeyDown() && mc.playerController.curBlockDamageMP == 0F
                || rightValue.get() && mc.gameSettings.keyBindUseItem.isKeyDown() && !mc.thePlayer.isUsingItem())) {
            if (random.nextBoolean()) {
                mc.thePlayer.rotationYaw += random.nextBoolean() ? -RandomUtils.nextFloat(0F, 1F) : RandomUtils.nextFloat(0F, 1F);
            }

            if (random.nextBoolean()) {
                mc.thePlayer.rotationPitch += random.nextBoolean() ? -RandomUtils.nextFloat(0F, 1F) : RandomUtils.nextFloat(0F, 1F);

                // Make sure pitch is not going into unlegit values
                if (mc.thePlayer.rotationPitch > 90) {
                    mc.thePlayer.rotationPitch = 90F;
                } else if (mc.thePlayer.rotationPitch < -90) {
                    mc.thePlayer.rotationPitch = -90F;
                }
            }
        }

    }
}