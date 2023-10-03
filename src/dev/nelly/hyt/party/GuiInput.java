package dev.nelly.hyt.party;

import dev.nelly.hyt.party.Sender;
import dev.nelly.hyt.party.VexViewButton;
import me.memorial.ui.font.Fonts;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

public class GuiInput extends GuiScreen {
    private GuiButton confirm;
    private GuiTextField inputField;
    private final String fieldId;
    private final VexViewButton confirmButton;

    public GuiInput(String fieldId, VexViewButton confirmButton) {
        this.fieldId = fieldId;
        this.confirmButton = confirmButton;
    }

    @Override
    public void initGui() {
        final FontRenderer fontRenderer = mc.fontRendererObj;
        inputField = new GuiTextField(0, fontRenderer, width / 2 - 50, height / 2 - 30, 100, 20);
        inputField.setMaxStringLength(32);
        inputField.setFocused(true);
        confirm = new GuiButton(0, width / 2, height / 2 + 30, 50, 20, confirmButton.getName());
        buttonList.add(confirm);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(new ResourceLocation("liquidbounce/hyt/background.png"));
        drawTexturedModalRect(width / 2 - 100, height / 2 - 81, 0, 0, 200, 162);
        Fonts.font35.drawCenteredString( "花雨庭组队系统", width / 2, height / 2 - 72, new Color(216, 216, 216).getRGB());
        GlStateManager.disableBlend();
        inputField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == confirm) {
            Sender.joinParty(inputField.getText(), fieldId, confirmButton.getId());
        }
        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        inputField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
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
