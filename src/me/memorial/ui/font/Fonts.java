package me.memorial.ui.font;

import me.memorial.utils.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Fonts {

    @FontDetails(fontName = "Roboto Medium", fontSize = 14)
    public static GameFontRenderer font14;
    @FontDetails(fontName = "Roboto Medium", fontSize = 18)
    public static GameFontRenderer font18;
    @FontDetails(fontName = "Roboto Medium", fontSize = 20)
    public static GameFontRenderer font20;
    @FontDetails(fontName = "Roboto Medium", fontSize = 30)
    public static GameFontRenderer font30;
    @FontDetails(fontName = "Roboto Medium", fontSize = 35)
    public static GameFontRenderer font35;

    @FontDetails(fontName = "Roboto Medium", fontSize = 40)
    public static GameFontRenderer font40;

    @FontDetails(fontName = "Roboto Bold", fontSize = 180)
    public static GameFontRenderer fontBold180;
    @FontDetails(fontName = "Medium", fontSize = 25)
    public static GameFontRenderer MEDIUM_25;
    @FontDetails(fontName = "Medium", fontSize = 27)
    public static GameFontRenderer MEDIUM_27;
    @FontDetails(fontName = "Medium", fontSize = 35)
    public static GameFontRenderer MEDIUM_35;
    @FontDetails(fontName = "Medium", fontSize = 40)
    public static GameFontRenderer MEDIUM_40;
    @FontDetails(fontName = "Medium", fontSize = 45)
    public static GameFontRenderer MEDIUM_45;
    @FontDetails(fontName = "Minecraft Font")
    public static final FontRenderer minecraftFont = Minecraft.getMinecraft().fontRendererObj;

    private static final List<GameFontRenderer> CUSTOM_FONT_RENDERERS = new ArrayList<>();

    public static void loadFonts() {
        long l = System.currentTimeMillis();

        me.memorial.utils.ClientUtils.loginfo("Loading Fonts.");

        font14 = new GameFontRenderer(getFont("sfui.ttf", 14));
        font18 = new GameFontRenderer(getFont("sfui.ttf", 18));
        font20 = new GameFontRenderer(getFont("sfui.ttf", 20));
        font30 = new GameFontRenderer(getFont("sfui.ttf", 30));
        font35 = new GameFontRenderer(getFont("sfui.ttf", 35));
        font40 = new GameFontRenderer(getFont("sfui.ttf", 40));

        MEDIUM_25 = new GameFontRenderer(getFont("Medium.ttf", 25));
        MEDIUM_27 = new GameFontRenderer(getFont("Medium.ttf", 27));
        MEDIUM_35 = new GameFontRenderer(getFont("Medium.ttf", 35));
        MEDIUM_40 = new GameFontRenderer(getFont("Medium.ttf", 40));
        MEDIUM_45 = new GameFontRenderer(getFont("Medium.ttf", 45));

        fontBold180 = new GameFontRenderer(getFont("sfuibold.ttf", 180));

        me.memorial.utils.ClientUtils.loginfo("Loaded Fonts. (" + (System.currentTimeMillis() - l) + "ms)");
    }


    public static FontRenderer getFontRenderer(final String name, final int size) {
        for(final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                final Object o = field.get(null);

                if(o instanceof FontRenderer) {
                    final FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    if(fontDetails.fontName().equals(name) && fontDetails.fontSize() == size)
                        return (FontRenderer) o;
                }
            }catch(final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (final GameFontRenderer liquidFontRenderer : CUSTOM_FONT_RENDERERS) {
            final Font font = liquidFontRenderer.getDefaultFont().getFont();

            if(font.getName().equals(name) && font.getSize() == size)
                return liquidFontRenderer;
        }

        return minecraftFont;
    }

    public static Object[] getFontDetails(final FontRenderer fontRenderer) {
        for(final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                final Object o = field.get(null);

                if(o.equals(fontRenderer)) {
                    final FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    return new Object[] {fontDetails.fontName(), fontDetails.fontSize()};
                }
            }catch(final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (fontRenderer instanceof GameFontRenderer) {
            final Font font = ((GameFontRenderer) fontRenderer).getDefaultFont().getFont();

            return new Object[] {font.getName(), font.getSize()};
        }

        return null;
    }

    public static List<FontRenderer> getFonts() {
        final List<FontRenderer> fonts = new ArrayList<>();

        for(final Field fontField : Fonts.class.getDeclaredFields()) {
            try {
                fontField.setAccessible(true);

                final Object fontObj = fontField.get(null);

                if(fontObj instanceof FontRenderer) fonts.add((FontRenderer) fontObj);
            }catch(final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        fonts.addAll(Fonts.CUSTOM_FONT_RENDERERS);

        return fonts;
    }

    private static Font getFont(final String fontName, final int size) {
        Font font;
        final Minecraft mc = Minecraft.getMinecraft();
        try {
                InputStream inputStream;
                if (fontName.endsWith(".ttf") || fontName.endsWith(".otf")) {
                    inputStream = mc.getResourceManager().getResource(new ResourceLocation("liquidbounce/font/" + fontName)).getInputStream();
                } else {
                    inputStream = mc.getResourceManager().getResource(new ResourceLocation("liquidbounce/font/" + fontName + ".ttf")).getInputStream();
                }
                Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            font = awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            font = new Font("default", Font.PLAIN, size);
        }

        return font;
    }
}