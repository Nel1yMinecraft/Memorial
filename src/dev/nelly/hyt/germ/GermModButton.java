package dev.nelly.hyt.germ;

import io.netty.buffer.Unpooled;
import me.memorial.ui.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import java.awt.*;

/**
 * @projectName: MIN
 * @author: vlouboos
 * @date: 2023-07-21 15:18:30
 */
public class GermModButton {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final String path;
    private final String text;
    private boolean hovered;

    public GermModButton(String path, String text) {
        this.path = path;
        this.text = text;
        hovered = false;
    }

    public void drawButton(String parentUuid, int x, int y, int mouseX, int mouseY) {
        if (mousePressed(x - 50, y - 10, x + 50, y + 10, mouseX, mouseY)) {
            if (!hovered) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13))
                        .writeString(parentUuid)
                        .writeString(path)
                        .writeInt(2))
                ));
                hovered = true;
            }
        } else if (hovered) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13))
                    .writeString(parentUuid)
                    .writeString(path)
                    .writeInt(3))
            ));
            hovered = false;
        }
        int startX = x - 50;
        int endX = x + 50;
        int startY = y - 10;
        int endY = y + 10;
        Gui.drawRect(startX, startY, endX, endY, new Color(0, 0, 0, 102).getRGB());
        Gui.drawRect(startX, startY, (int) (startX + 0.5D), endY, new Color(44, 44, 44, 102).getRGB());
        Gui.drawRect((int) (startX + 0.5D), (int) (endY - 0.5D), (int) (endX - 0.5D), endY, new Color(44, 44, 44, 102).getRGB());
        Gui.drawRect((int) (endX - 0.5D), startY, endX, endY, new Color(44, 44, 44, 102).getRGB());
        Gui.drawRect((int) (startX + 0.5D), startY, (int) (endX - 0.5D), (int) (startY + 0.5), new Color(44, 44, 44, 102).getRGB());
        Fonts.font35.drawCenteredString(text.replace("\2478", ""), x, startY + 6, new Color(216, 216, 216).getRGB());
    }

    public void mouseClicked(String parentUuid) {
        if (hovered) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13))
                    .writeString(parentUuid)
                    .writeString(path)
                    .writeInt(0))
            ));
            mc.getNetHandler().getNetworkManager().sendPacket(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(11))
                    .writeString(parentUuid)
            ));
            mc.displayGuiScreen(null);
        }
    }
    public static boolean mousePressed(int left, int top, int right, int bottom, int mouseX, int mouseY) {
        return mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom;
    }
}
