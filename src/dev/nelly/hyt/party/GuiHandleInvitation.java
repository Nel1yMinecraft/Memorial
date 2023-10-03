import dev.nelly.hyt.party.Sender;
import dev.nelly.hyt.party.VexViewButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

public class GuiHandleInvitation extends GuiScreen {
    private final VexViewButton acceptButton;
    private final VexViewButton denyButton;

    public GuiHandleInvitation(VexViewButton acceptButton, VexViewButton denyButton) {
        this.acceptButton = acceptButton;
        this.denyButton = denyButton;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 50, height / 2 - 20, 100, 20, acceptButton.getName()) {
            @Override
            public void mouseReleased(int mouseX, int mouseY) {
                Sender.clickButton(acceptButton.getId());
            }
        });
        buttonList.add(new GuiButton(1, width / 2 - 50, height / 2 + 20, 100, 20, denyButton.getName()) {
            @Override
            public void mouseReleased(int mouseX, int mouseY) {
                Sender.clickButton(denyButton.getId());
            }
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(new ResourceLocation("liquidbounce/hyt/background.png"));
        drawTexturedModalRect(width / 2 - 100, height / 2 - 81, 0, 0, 200, 162);
        drawCenteredString(fontRendererObj, "花雨庭组队系统", width / 2, height / 2 - 72, new Color(216, 216, 216).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.disableBlend();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
