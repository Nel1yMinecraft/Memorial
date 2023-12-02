package me.memorial.module.modules.render.targethud.impl;

import me.memorial.Memorial;
import me.memorial.module.modules.client.TargetHUD;
import me.memorial.module.modules.render.targethud.Style;
import me.memorial.ui.font.Fonts;
import me.memorial.utils.animations.ContinualAnimation;
import me.memorial.utils.render.RenderUtils;
import me.memorial.utils.render.Stencil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MoonTargetHUD extends Style {
    public MoonTargetHUD() {
        super("Moon");
    }
    private final ContinualAnimation continualAnimation = new ContinualAnimation();

    @Override
    public void render(int x, int y,int alpha, EntityPlayer target) {
        float nameWidth = Fonts.MEDIUM_40.getStringWidth(target.getName());
        float space = (nameWidth - 40) / 100;
        float barWidth = (nameWidth + 5F + space) * (target.getHealth() / target.getMaxHealth()) + 6F;
        setWidth(40 + nameWidth + 10);
        setHeight(36);
        continualAnimation.animate(barWidth,20);
        RenderUtils.drawRoundedRect(x, y,40 + nameWidth + 10,36, 10,new Color(0,0,0,50).getRGB());
        Fonts.MEDIUM_40.drawString(target.getName(), x + 38, y + 5, Color.WHITE.getRGB());
        Fonts.MEDIUM_27.drawString(String.format("%.1f", target.getHealth()) + "HP", x + 38.5F, y + 17, Color.WHITE.getRGB());
        RenderUtils.drawRoundedRect(x + 36, y + 24, nameWidth + 12 + space, 10, 8,new Color(0,0,0,100).getRGB());
        TargetHUD targetHUD = (TargetHUD) Memorial.moduleManager.getModule(TargetHUD.class);
        RenderUtils.drawRoundedRect(x + 36.5F, y + 24.5F, barWidth, 9F, 8,targetHUD.getColor().getRGB());
        if (target != null) {
            Stencil.write(false);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderUtils.fastRoundedRect(x + 1.5f, y + 1.5f, x + 33.5F, y + 33.5F, 8F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            Stencil.erase(true);
            drawHead(((AbstractClientPlayer) target).getLocationSkin(), x + 1.5f, y + 1.5f, 1f, 32, 32, 1f, 1f, 1F);
            Stencil.dispose();
        }
    }

    @Override
    public void renderEffects(int x, int y,int alpha) {
        RenderUtils.drawRoundedRect(x - 1, y - 1,getWidth() + 2,getHeight() + 2, 10,new Color(0,0,0).getRGB());
    }
}
