package me.memorial.module.modules.render;

import lombok.Getter;
import me.memorial.events.*;

import me.memorial.events.impl.misc.ScreenEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.ui.font.Fonts;
import me.memorial.value.BoolValue;
import me.memorial.value.FloatValue;
import me.memorial.value.FontValue;
import me.memorial.value.IntegerValue;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(name = "HUD", description = "Toggles visibility of the HUD.", category = ModuleCategory.CLIENT, array = false)
public class HUD extends Module {

    @Getter
    private static HUD instance;

    public static final FloatValue rainbowStartValue = new FloatValue("RainbowStart", 0.41f, 0f, 1f);
    public static final FloatValue rainbowStopValue = new FloatValue("RainbowStop", 0.58f, 0f, 1f);
    public static final IntegerValue rainbowSpeedValue = new IntegerValue("RainbowSpeed", 1500, 500, 7000);
    public static final FloatValue rainbowSaturationValue = new FloatValue("RainbowSaturation", 0.7f, 0f, 1f);
    public static final FloatValue rainbowBrightnessValue = new FloatValue("RainbowBrightness", 1f, 0f, 1f);

    public HUD() {
        setState(true);
        instance = this;
    }

    public final BoolValue blackHotbarValue = new BoolValue("BlackHotbar", true);
    public final BoolValue inventoryParticle = new BoolValue("InventoryParticle", false);
    public final BoolValue inventoryPaiMon = new BoolValue("inventoryPaiMon", false);
    private final BoolValue blurValue = new BoolValue("Blur", false);
    public int photoIndex = 0;
    public final FontValue fontChatValue = new FontValue("FontChat", Fonts.font35) {
        @Override
        protected void onChanged(FontRenderer oldValue, FontRenderer newValue) {
            if(HUD.getInstance().getState()){
                GuiNewChat.font = newValue;
            }
            super.onChanged(oldValue, newValue);
        }
    };

    @EventTarget(ignoreCondition = true)
    public void onScreen(final ScreenEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null)
            return;

        if (getState() && blurValue.get() && !mc.entityRenderer.isShaderActive() && event.getGuiScreen() != null &&
                !(event.getGuiScreen() instanceof GuiChat))
            mc.entityRenderer.loadShader(new ResourceLocation("liquidbounce/blur.json"));
        else if (mc.entityRenderer.getShaderGroup() != null &&
                mc.entityRenderer.getShaderGroup().getShaderGroupName().contains("liquidbounce/blur.json"))
            mc.entityRenderer.stopUseShader();
    }

    public void onEnable(){
        GuiNewChat.font = fontChatValue.get();
    }

    public void onDisable(){
        GuiNewChat.font = mc.fontRendererObj;
    }
}
