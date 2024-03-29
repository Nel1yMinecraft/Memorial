package me.memorial.module.modules.render;

import dev.Rat.utils.motionblur.MotionBlurResourceManager;
import me.memorial.events.EventTarget;
import me.memorial.events.impl.misc.TickEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.ClientUtils;
import me.memorial.value.IntegerValue;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.Map;

@ModuleInfo(
        name = "MotionBlur",
        description = "Render view.",
        category = ModuleCategory.RENDER
)
public class MotionBlur extends Module {
    public static IntegerValue MOTION_BLUR_AMOUNT = new IntegerValue("BlurAmount", 2, 1, 10);
    int lastValue = 0;
    private Map<String, MotionBlurResourceManager> domainResourceManagers;
    @Override
    public void onDisable() {
        mc.entityRenderer.stopUseShader();
    }
    @Override
    public void onEnable() {
        if(this.domainResourceManagers == null) {
            try {
                Field[] fields = SimpleReloadableResourceManager.class.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getType() == Map.class) {
                        field.setAccessible(true);
                        this.domainResourceManagers = (Map<String, MotionBlurResourceManager>) field.get(mc.getResourceManager());
                        break;
                    }
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        if(!this.domainResourceManagers.containsKey("motionblur")) {
            this.domainResourceManagers.put("motionblur", new MotionBlurResourceManager());
        }

        if(isFastRenderEnabled())
            ClientUtils.disableFastRender();
        this.lastValue = MOTION_BLUR_AMOUNT.get();
        applyShader();
    }

    public boolean isFastRenderEnabled() {
        try {
            Field fastRender = GameSettings.class.getDeclaredField("ofFastRender");
            return fastRender.getBoolean(mc.gameSettings);
        } catch (Exception exception) {
            return false;
        }
    }
    public void applyShader() {
        mc.entityRenderer.loadShader(new ResourceLocation("motionblur", "motionblur"));
    }
    @EventTarget
    public void onTick(TickEvent event) {
        if((!mc.entityRenderer.isShaderActive() || this.lastValue != MOTION_BLUR_AMOUNT.get()) && mc.theWorld!= null && !isFastRenderEnabled()) {
            this.lastValue = MOTION_BLUR_AMOUNT.get();
            applyShader();
        }
        if(this.domainResourceManagers == null) {
            try {
                Field[] fields = SimpleReloadableResourceManager.class.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getType() == Map.class) {
                        field.setAccessible(true);
                        this.domainResourceManagers = (Map<String, MotionBlurResourceManager>) field.get(mc.getResourceManager());
                        break;
                    }
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
        if(!this.domainResourceManagers.containsKey("motionblur")) {
            this.domainResourceManagers.put("motionblur", new MotionBlurResourceManager());
        }
        if(isFastRenderEnabled())
            ClientUtils.disableFastRender();
    }
}
