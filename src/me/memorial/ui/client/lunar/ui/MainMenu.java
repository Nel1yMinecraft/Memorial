package me.memorial.ui.client.lunar.ui;

import java.awt.Color;

import me.memorial.Memorial;
import me.memorial.ui.client.GuiBackground;
import me.memorial.ui.client.altmanager.GuiAltManager;
import me.memorial.ui.client.lunar.font.FontUtil;
import me.memorial.ui.client.lunar.ui.buttons.ImageButton;
import me.memorial.ui.client.lunar.ui.buttons.MainButton;
import me.memorial.ui.client.lunar.ui.buttons.QuitButton;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;


import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class MainMenu extends GuiScreen {
	
	private ResourceLocation logo;
	
	private MainButton btnSingleplayer;
	private MainButton btnMultiplayer;
    private MainButton btnAltManager;

    private ImageButton btnLunarOptions;
	private ImageButton btnCosmetics;
	private ImageButton btnMinecraftOptions;
	private ImageButton btnLanguage;
	
	private QuitButton btnQuit;
	
	private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[] {
            new ResourceLocation("lunar/panorama/panorama_0.png"),
            new ResourceLocation("lunar/panorama/panorama_1.png"),
            new ResourceLocation("lunar/panorama/panorama_2.png"),
            new ResourceLocation("lunar/panorama/panorama_3.png"),
            new ResourceLocation("lunar/panorama/panorama_4.png"),
            new ResourceLocation("lunar/panorama/panorama_5.png")
    };

    private static int panoramaTimer;
    private ResourceLocation backgroundTexture;

    @Override
    public void initGui() {
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", new DynamicTexture(256, 256));
        
        this.logo = new ResourceLocation("lunar/logo.png");
        
        this.btnSingleplayer = new MainButton("S I N G L E P L A Y E R", this.width / 2 - 66, this.height / 2);
        this.btnMultiplayer = new MainButton("M U L T I P L A Y E R", this.width / 2 - 66, this.height / 2 + 15);
        this.btnAltManager = new MainButton("A L T M A N A G E R", this.width / 2 - 66, this.height / 2 + 15 + 15);
        
        int yPos = this.height - 20;
        this.btnLunarOptions = new ImageButton("BACKROUND", new ResourceLocation("lunar/icons/lunar.png"), this.width / 2 - 30, yPos);
        this.btnCosmetics = new ImageButton("ALTMANNGER", new ResourceLocation("lunar/icons/cosmetics.png"), this.width / 2 - 15, yPos);
        this.btnMinecraftOptions = new ImageButton("MINECRAFT SETTINGS", new ResourceLocation("lunar/icons/cog.png"), this.width / 2, yPos);
        this.btnLanguage = new ImageButton("LANGUAGE", new ResourceLocation("lunar/icons/globe.png"), this.width / 2 + 15, yPos);
        
        this.btnQuit = new QuitButton(this.width - 17, 7);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawLunarBackground(mouseX, mouseY, partialTicks,backgroundTexture);

        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(logo);
        Gui.drawModalRectWithCustomSizedTexture(this.width / 2 - 25, this.height / 2 - 68, 0, 0, 49, 49, 49, 49);

        //        FontUtil.TITLE.getFont().drawCenteredString("LUNAR CLIENT", this.width / 2 - 0.25F, this.height / 2 - 18, new Color(30, 30, 30, 70).getRGB());
        //        FontUtil.TITLE.getFont().drawCenteredString("LUNAR CLIENT", this.width / 2, this.height / 2 - 19, -1);

        FontUtil.TITLE.getFont().drawCenteredString("MEMORIAL CLIENT", this.width / 2 - 0.25F, this.height / 2 - 18, new Color(30, 30, 30, 70).getRGB());
        FontUtil.TITLE.getFont().drawCenteredString("MEMORIAL CLIENT", this.width / 2, this.height / 2 - 19, -1);
        
        this.btnSingleplayer.drawButton(mouseX, mouseY);
        this.btnMultiplayer.drawButton(mouseX, mouseY);
        this.btnAltManager.drawButton(mouseX, mouseY);

        this.btnLunarOptions.drawButton(mouseX, mouseY);
        this.btnCosmetics.drawButton(mouseX, mouseY);
        this.btnMinecraftOptions.drawButton(mouseX, mouseY);
        this.btnLanguage.drawButton(mouseX, mouseY);
        
        this.btnQuit.drawButton(mouseX, mouseY);
        
        String s = "Copyright Mojang Studios. Do not distribute!";
        //        FontUtil.TEXT.getFont().drawString("Lunar Client 1.8.9 ("+ Memorial.CLIENT_VERSION +"/master)", 7, this.height - 11, new Color(255, 255, 255, 100).getRGB());
        FontUtil.TEXT.getFont().drawString("Memorial Client 1.8.9 ("+ Memorial.CLIENT_VERSION +"/master)", 7, this.height - 11, new Color(255, 255, 255, 100).getRGB());
        FontUtil.TEXT.getFont().drawString(s, this.width - FontUtil.TEXT.getFont().getWidth(s) - 6, this.height - 11, new Color(255, 255, 255, 100).getRGB());
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        ++panoramaTimer;
        super.updateScreen();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {

        //boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        if (this.btnSingleplayer.hoverFade >0){
            mc.displayGuiScreen(new GuiSelectWorld(this));
        }
        if (this.btnMultiplayer.hoverFade >0) {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (this.btnAltManager.hoverFade >0) {
            mc.displayGuiScreen(new GuiAltManager(this));
        }
        if (this.btnQuit.hoverFade >0){
            mc.shutdown();
        }
        if (this.btnMinecraftOptions.hoverFade >0) {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        }
        if (this.btnLanguage.hoverFade >0) {
            mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }
        if (this.btnLunarOptions.hoverFade >0) {
            mc.displayGuiScreen(new GuiBackground(this));
        }
        if (this.btnCosmetics.hoverFade >0 ){
            mc.displayGuiScreen(new GuiAltManager(this));
        }


    }

}
