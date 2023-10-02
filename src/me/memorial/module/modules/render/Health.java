package me.memorial.module.modules.render;

import me.memorial.events.EventTarget;
import me.memorial.events.Render2DEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.client.math.MathUtils;
import net.minecraft.client.gui.ScaledResolution;
import java.awt.Color;

@ModuleInfo(name = "Health", description = "Shows your health under the crosshair", category = ModuleCategory.RENDER, array = false)
public class Health extends Module {

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);

        String health = String.valueOf(MathUtils.round(mc.thePlayer.getHealth(), 1));

        float x = sr.getScaledWidth() / 2F + 10;
        float y = sr.getScaledHeight() / 2F + 10;

        if (mc.thePlayer.getHealth() < 14 && mc.thePlayer.getHealth() >= 6) {
            mc.fontRendererObj.drawString(health, (int)x, (int)y, Color.ORANGE.getRGB());
        } else if (mc.thePlayer.getHealth() < 6) {
            mc.fontRendererObj.drawString(health, (int)x, (int)y, Color.RED.getRGB());
        } else {
            mc.fontRendererObj.drawString(health, (int)x, (int)y, Color.GREEN.getRGB());
        }
    }
}
