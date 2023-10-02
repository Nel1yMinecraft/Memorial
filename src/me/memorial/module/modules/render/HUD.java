package me.memorial.module.modules.render;

import me.memorial.events.*;
import me.memorial.Memorial;

import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.ui.client.hud.designer.GuiHudDesigner;
import me.memorial.ui.font.Fonts;
import me.memorial.value.BoolValue;
import me.memorial.value.FontValue;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(name = "HUD", description = "Toggles visibility of the HUD.", category = ModuleCategory.RENDER, array = false)
public class HUD extends Module {
    private static HUD instance;

    public static HUD getInstance(){
        return instance;
    }

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

    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        if (mc.currentScreen instanceof GuiHudDesigner)
            return;

        Memorial.hud.render(false);
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        Memorial.hud.update();
    }

    @EventTarget
    public void onKey(final KeyEvent event) {
        Memorial.hud.handleKey('a', event.getKey());
    }

    @EventTarget(ignoreCondition = true)
    public void onScreen(final ScreenEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null)
            return;

        if (getState() && blurValue.get() && !mc.entityRenderer.isShaderActive() && event.getGuiScreen() != null &&
                !(event.getGuiScreen() instanceof GuiChat || event.getGuiScreen() instanceof GuiHudDesigner))
            mc.entityRenderer.loadShader(new ResourceLocation(Memorial.CLIENT_NAME.toLowerCase() + "/blur.json"));
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
