package me.memorial.module.modules.render.targethud;

import lombok.Getter;
import lombok.Setter;
import me.memorial.module.modules.render.targethud.impl.MoonTargetHUD;
import me.memorial.utils.Utils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glColor4f;

public abstract class Style implements Utils {
    @Getter
    private final String name;
    public Style(String name) {
        this.name = name;
    }
    @Getter
    @Setter
    private float width,height;
    public abstract void render(int x, int y,int alpha, EntityPlayer target);
    public abstract void renderEffects(int x, int y,int alpha);
    private static final HashMap<Class<? extends Style>, Style> targetHuds = new HashMap<>();

    public static Style get(String name) {
        return targetHuds.values().stream().filter(hud -> hud.getName().equals(name)).findFirst().orElse(null);
    }

    public static <T extends Style> T get(Class<T> clazz) {
        return (T) targetHuds.get(clazz);
    }

    public static void init() {
        targetHuds.put(MoonTargetHUD.class, new MoonTargetHUD());
    }
    public void drawHead(ResourceLocation skin, float x, float y , float scale , int width, int height, float red, float green, float blue) {
        glPushMatrix();
        glTranslatef(x, y, 0F);
        glScalef(scale, scale, scale);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor4f(red, green, blue, 1F);
        mc.getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(0, 0, 8F, 8F, 8, 8, width, height,
                64F, 64F);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glPopMatrix();
        glColor4f(1f, 1f, 1f, 1f);
    }
}
