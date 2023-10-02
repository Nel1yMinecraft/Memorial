/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.LiquidBounceStyle;
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.NullStyle;
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.SlowlyStyle;
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.dropdown.DropdownGUI;
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.novoline.ClickyUI;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@ModuleInfo(name = "ClickGUI", description = "Opens the ClickGUI.", category = ModuleCategory.RENDER, keyBind = Keyboard.KEY_RSHIFT)
public class ClickGUI extends Module {
    private final ListValue styleValue = new ListValue("Style",
            new String[]{
                    "DropDown",
                    "Novoline",
                    "LiquidBounce",
                    "Null",
                    "Slowly"},
            "LiquidBounce") {
        @Override
        protected void onChanged(final String oldValue, final String newValue) {
            updateStyle();
        }
    };

    public final FloatValue scaleValue = new FloatValue("Scale", 1F, 0.4F, 2F);
    public final IntegerValue maxElementsValue = new IntegerValue("MaxElements", 15, 1, 20);

    public static final ListValue colorModeValue = new ListValue("Color", new String[]{"Custom", "Sky", "Rainbow", "LiquidSlowly", "Fade", "Mixer"}, "Custom");
    public static final IntegerValue colorRedValue = new IntegerValue("Red", 0, 0, 255);
    public static final IntegerValue colorGreenValue = new IntegerValue("Green", 160, 0, 255);
    public static final IntegerValue colorBlueValue = new IntegerValue("Blue", 255, 0, 255);

    public final FloatValue scale = new FloatValue("AstolfoScale", 1f, 0f, 10f);
    public final FloatValue scroll = new FloatValue("Scroll", 20f, 0f, 200f);;
    public final BoolValue getGetClosePrevious = new BoolValue("ClosePrevious",false);
    DropdownGUI dropdownGUI = null;

    public static Color generateColor() {
        Color c = new Color(255, 255, 255, 255);
        switch (colorModeValue.get().toLowerCase()) {
            case "custom":
                c = new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
                break;
                /*
            case "rainbow":
                c = new Color(RenderUtils.getRainbowOpaque(mixerSecondsValue.get(), saturationValue.get(), brightnessValue.get(), 0));
                break;
            case "sky":
                c = RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get());
                break;
            case "liquidslowly":
                c = ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get());
                break;
            case "fade":
                c = ColorUtils.fade(new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 0, 100);
                break;
            case "mixer":
                c = ColorMixer.getMixedColor(0, mixerSecondsValue.get());
                break;
                 */
        }
        return c;
    }

    private DropdownGUI getDropdownGUI() {
        if (dropdownGUI == null)
            dropdownGUI = new DropdownGUI();
        return dropdownGUI;
    }

    @Override
    public void onEnable() {
        switch (styleValue.get().toLowerCase()) {
            case "novoline":
                mc.displayGuiScreen(new ClickyUI());
                this.setState(false);
                break;
            case "dropdown":
                mc.displayGuiScreen(getDropdownGUI());
                break;
            default:
                updateStyle();
                mc.displayGuiScreen(LiquidBounce.clickGui);
                mc.displayGuiScreen(LiquidBounce.clickGui);
        }
    }
    @Override
    public void onDisable() {
        if (styleValue.get().toLowerCase().contains("novoline")) {
            mc.displayGuiScreen(new ClickyUI());
            this.setState(false);
        }
        if (styleValue.get().toLowerCase().contains("dropdown")) {
            mc.displayGuiScreen(getDropdownGUI());
        } else {
            updateStyle();
            mc.displayGuiScreen(LiquidBounce.clickGui);
            mc.displayGuiScreen(LiquidBounce.clickGui);
        }
    }

    private void updateStyle() {
        switch(styleValue.get().toLowerCase()) {
            case "liquidbounce":
                LiquidBounce.clickGui.style = new LiquidBounceStyle();
                break;
            case "null":
                LiquidBounce.clickGui.style = new NullStyle();
                break;
            case "slowly":
                LiquidBounce.clickGui.style = new SlowlyStyle();
                break;
        }
    }
}