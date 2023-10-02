package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.client.math.MathUtils;
import net.ccbluex.liquidbounce.value.FontValue;
import net.minecraft.client.gui.ScaledResolution;
import java.awt.Color;

@ModuleInfo(name = "Health", description = "Shows your health under the crosshair", category = ModuleCategory.RENDER, array = false)
public class Health extends Module {
    final FontValue fontValue = new FontValue("Fonts", Fonts.font40);

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);

        String health = String.valueOf(MathUtils.round(mc.thePlayer.getHealth(), 1));

        float x = sr.getScaledWidth() / 2F + 10;
        float y = sr.getScaledHeight() / 2F + 10;

        if (mc.thePlayer.getHealth() < 14 && mc.thePlayer.getHealth() >= 6) {
            fontValue.get().drawString(health, (int)x, (int)y, Color.ORANGE.getRGB());
        } else if (mc.thePlayer.getHealth() < 6) {
            fontValue.get().drawString(health, (int)x, (int)y, Color.RED.getRGB());
        } else {
            fontValue.get().drawString(health, (int)x, (int)y, Color.GREEN.getRGB());
        }
    }
}
