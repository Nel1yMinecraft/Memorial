package dev.nelly.hyt.hytprotocol;

import dev.nelly.hyt.Protocol;
import dev.nelly.hyt.menu.MenuButton;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import org.yaml.snakeyaml.Yaml;


public class GermGui extends GuiScreen {
    private final float width;
    private final float height;
    private ScaledResolution sr;
    Map<String, Object> btnmap;
    private final List<MenuButton> buttons = new ArrayList();
    private List<String> buttonTextList = new LinkedList();

    public GermGui(float width, float height, Map<String, Object> btnmap) {
        this.width = width;
        this.height = height;
        this.btnmap = btnmap;
    }

    public void initGui() {
        this.sr = new ScaledResolution(this.mc);
        Iterator var1 = this.btnmap.values().iterator();

        while(true) {
            Object scrollablePart;
            do {
                if (!var1.hasNext()) {
                    this.buttons.forEach(MenuButton::initGui);
                    return;
                }

                scrollablePart = var1.next();
            } while(!(scrollablePart instanceof Map));

            Map<String, Object> newMap4 = (Map)scrollablePart;
            Map<String, Object> relativeParts = (Map)newMap4.get("relativeParts");
            Iterator var5 = relativeParts.entrySet().iterator();

            while(var5.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry)var5.next();
                Object value = entry.getValue();
                if (value instanceof Map) {
                    Map<String, Object> innerMap = (Map)value;
                    Object texts = innerMap.get("texts");
                    if (texts != null) {
                        String text = texts.toString().replace("[", "").replace("]", "").replace("§8", "");
                        this.buttonTextList.add(text);
                        this.buttons.add(new MenuButton(text));
                    }
                }
            }
        }
    }

    public String getToken(Map<String, Object> objectMap) {
        if (objectMap == null) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            Map<String, Object> newMap = (Map)objectMap.get(Protocol.field_3285);
            if (newMap == null) {
                return "";
            } else {
                Map<String, Object> newMap2 = null;
                String omg = "";

                String s;
                for(Iterator var6 = newMap.keySet().iterator(); var6.hasNext(); omg = s) {
                    s = (String)var6.next();
                    newMap2 = (Map)newMap.get(s);
                }

                sb.append(omg).append("$");
                Map<String, Object> scrollableParts = (Map)newMap2.get("scrollableParts");
                Map<String, Object> newMap4 = null;
                Iterator var8 = scrollableParts.keySet().iterator();
                if (var8.hasNext()) {
                    String s2 = (String)var8.next();
                    newMap4 = (Map)scrollableParts.get(s2);
                    sb.append(s2).append("$");
                }

                Map<String, Object> relativeParts = (Map)newMap4.get("relativeParts");
                Iterator var14 = relativeParts.keySet().iterator();
                if (var14.hasNext()) {
                    String s3 = (String)var14.next();
                    sb.append(s3);
                }

                return sb.toString();
            }
        }
    }

    public String getSecToken(Map<String, Object> objectMap) {
        if (objectMap == null) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            Map<String, Object> newMap = (Map)objectMap.get(Protocol.field_3285);
            if (newMap == null) {
                return "";
            } else {
                Map<String, Object> newMap2 = null;
                String omg = "";

                String s;
                for(Iterator var6 = newMap.keySet().iterator(); var6.hasNext(); omg = s) {
                    s = (String)var6.next();
                    newMap2 = (Map)newMap.get(s);
                }

                sb.append(omg).append("$");
                Map<String, Object> scrollableParts = (Map)newMap2.get("scrollableParts");
                s = null;
                Map<String, Object> newMap4 = (Map)scrollableParts.get(scrollableParts.keySet().stream().skip(1L).findFirst().get());
                sb.append((String)scrollableParts.keySet().stream().skip(1L).findFirst().get()).append("$");
                Map<String, Object> relativeParts = (Map)newMap4.get("relativeParts");
                Iterator var9 = relativeParts.keySet().iterator();
                if (var9.hasNext()) {
                    String s4 = (String)var9.next();
                    sb.append(s4);
                }

                return sb.toString();
            }
        }
    }

    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        float x = (float)this.sr.getScaledWidth() / 2.0F - this.width / 2.0F;
        float y = (float)this.sr.getScaledHeight() / 2.0F - this.height / 2.0F;
        float buttonWidth = 140.0F;
        float buttonHeight = 25.0F;
        int count = 0;

        for(Iterator var9 = this.buttons.iterator(); var9.hasNext(); count = (int)((float)count + buttonHeight + 5.0F)) {
            MenuButton button = (MenuButton)var9.next();
            button.x = (float)this.sr.getScaledWidth() / 2.0F - buttonWidth / 2.0F;
            button.y = (float)this.sr.getScaledHeight() / 2.0F - buttonHeight / 2.0F - 25.0F + (float)count;
            button.width = buttonWidth;
            button.height = buttonHeight;
            String first = (String)this.buttonTextList.get(0);
            AtomicReference<String> key = new AtomicReference("");
            button.clickAction = () -> {
                if (button.text.equals(first)) {
                    Map<String, Object> objectMapx = (Map)(new Yaml()).load(Protocol.field_955);
                    PacketBuffer packetBuffer3x = new PacketBuffer(Unpooled.buffer().writeInt(13));
                    packetBuffer3x.writeString(Protocol.field_3285);
                    packetBuffer3x.writeString(this.getToken(objectMapx));
                    packetBuffer3x.writeInt(0);
                    this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", packetBuffer3x));
                } else {
                    PacketBuffer packetBuffer3 = new PacketBuffer(Unpooled.buffer().writeInt(13));
                    Map objectMap;
                    if (this.btnmap != null) {
                        objectMap = (Map)this.btnmap.values().stream().skip(1L).findFirst().orElse((Object)null);
                        Map<String, Object> relativeParts = (Map)objectMap.get("relativeParts");
                        if (relativeParts != null) {
                            Map.Entry<String, Object> firstEntry = (Map.Entry)relativeParts.entrySet().stream().findFirst().orElse((Map.Entry<String, Object>) null);
                            if (firstEntry != null) {
                                key.set(firstEntry.getKey());
                            }
                        }
                    }

                    objectMap = (Map)(new Yaml()).load(Protocol.field_955);
                    packetBuffer3.writeString(Protocol.field_3285);
                    packetBuffer3.writeString(this.getSecToken(objectMap));
                    packetBuffer3.writeInt(0);
                    this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", packetBuffer3));
                    this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("germmod-netease", (new PacketBuffer(Unpooled.buffer().writeInt(11))).writeString((String)key.get())));
                }

            };
            button.drawScreen(p_drawScreen_1_, p_drawScreen_2_);
        }

    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.buttons.forEach((button) -> {
            button.mouseClicked(mouseX, mouseY, mouseButton);
        });
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
