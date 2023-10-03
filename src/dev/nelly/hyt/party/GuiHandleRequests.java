package dev.nelly.hyt.party;

import jdk.nashorn.internal.ir.RuntimeNode;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class GuiHandleRequests extends GuiScreen {
    private final ArrayList<Request> requests;

    public GuiHandleRequests(ArrayList<Request> requests) {
        this.requests = requests;
    }


    @Override
    public void initGui() {
        buttonList.clear();
        int i = -40;
        for (Request request : requests) {
            buttonList.add(new GuiButton(0, width / 2 + 45, height / 2 + i, 10, 10, "√") {
                @Override
                public void mouseReleased(int mouseX, int mouseY) {
                    Sender.clickButton(request.getAcceptId());
                    requests.remove(request);
                    if (requests.isEmpty()) {
                        mc.displayGuiScreen(null);
                    } else {
                        mc.displayGuiScreen(new GuiHandleRequests(requests));
                    }
                }
            });
            buttonList.add(new GuiButton(1, width / 2 + 70, height / 2 + i, 10, 10, "×") {
                @Override
                public void mouseReleased(int mouseX, int mouseY) {
                    Sender.clickButton(request.getDenyId());
                    requests.remove(request);
                    if (requests.isEmpty()) {
                        mc.displayGuiScreen(null);
                    } else {
                        mc.displayGuiScreen(new GuiHandleRequests(requests));
                    }
                }
            });
            i += 20;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "花雨庭组队系统", width / 2, height / 2 - 72, new Color(216, 216, 216).getRGB());
        int i = -44;
        for (Request request : requests) {
            fontRendererObj.drawString(request.getName(), width / 2 - 80, height / 2 + i, new Color(216, 216, 216).getRGB());
            super.drawScreen(mouseX, mouseY, partialTicks);
            i += 20;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
