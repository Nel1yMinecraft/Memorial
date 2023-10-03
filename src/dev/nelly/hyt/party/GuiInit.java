package dev.nelly.hyt.party;

import me.memorial.ui.font.Fonts;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

public class GuiInit extends GuiScreen {
    private GuiButton create;
    private GuiButton join;
    private final VexViewButton createButton;
    private final VexViewButton joinButton;

    public GuiInit(VexViewButton createButton, VexViewButton joinButton) {
        this.createButton = createButton;
        this.joinButton = joinButton;
    }

    @Override
    public void initGui() {
        create = new GuiButton(0, width / 2 - 50, height / 2 - 20, 100, 20, createButton.getName());
        join = new GuiButton(1, width / 2 - 50, height / 2 + 20, 100, 20, joinButton.getName());
        buttonList.add(create);
        buttonList.add(join);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(new ResourceLocation("liquidbounce/hyt/background.png"));
        drawTexturedModalRect(width / 2 - 100, height / 2 - 81, 0, 0, 200, 162);
        Fonts.font35.drawCenteredString("花雨庭组队系统", width / 2, height / 2 - 72, new Color(216, 216, 216).getRGB());
        GlStateManager.disableBlend();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == create) {
            Sender.clickButton(createButton.getId());
        } else if (button == join) {
            Sender.clickButton(joinButton.getId());
        }
        super.actionPerformed(button);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().begin(7, DefaultVertexFormats.POSITION_TEX);
        tessellator.getWorldRenderer().pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1)).endVertex();
        tessellator.getWorldRenderer().pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1)).endVertex();
        tessellator.getWorldRenderer().pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        tessellator.getWorldRenderer().pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        tessellator.draw();
    }
}
