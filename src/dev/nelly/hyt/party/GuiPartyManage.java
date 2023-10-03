package dev.nelly.hyt.party;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;

public class GuiPartyManage extends GuiScreen {
    private GuiButton leave;
    private GuiButton disband;
    private GuiButton invite;
    private GuiButton request;
    private final VexViewButton leaveButton;
    private final VexViewButton disbandButton;
    private final VexViewButton inviteButton;
    private final VexViewButton requestButton;

    public GuiPartyManage(VexViewButton leaveButton, VexViewButton disbandButton, VexViewButton inviteButton, VexViewButton requestButton) {
        this.leaveButton = leaveButton;
        this.disbandButton = disbandButton;
        this.inviteButton = inviteButton;
        this.requestButton = requestButton;
        disband = null;
        invite = null;
        request = null;
    }

    @Override
    public void initGui() {
        if (inviteButton != null) {
            leave = new GuiButton(0, width / 2 - 50, height / 2 - 40, 100, 20, leaveButton.getName());
            buttonList.add(leave);
            disband = new GuiButton(1, width / 2 - 50, height / 2 - 10, 100, 20, disbandButton.getName());
            buttonList.add(disband);
            invite = new GuiButton(2, width / 2 - 50, height / 2 + 20, 100, 20, inviteButton.getName());
            buttonList.add(invite);
            request = new GuiButton(3, width / 2 - 50, height / 2 + 50, 100, 20, requestButton.getName());
            buttonList.add(request);
        } else {
            leave = new GuiButton(0, width / 2 - 50, height / 2 - 20, 100, 20, leaveButton.getName());
            buttonList.add(leave);
            if (disbandButton != null) {
                disband = new GuiButton(1, width / 2 - 50, height / 2 + 20, 100, 20, disbandButton.getName());
                buttonList.add(disband);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "花雨庭组队系统", width / 2, height / 2 - 72, new Color(216, 216, 216).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == leave) {
            Sender.clickButton(leaveButton.getId());
        } else if (button == disband) {
            Sender.clickButton(disbandButton.getId());
        } else if (button == invite) {
            Sender.clickButton(inviteButton.getId());
        } else if (button == request) {
            Sender.clickButton(requestButton.getId());
        }
        super.actionPerformed(button);
    }
}
