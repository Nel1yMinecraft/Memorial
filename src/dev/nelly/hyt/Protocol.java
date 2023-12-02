package dev.nelly.hyt;

import dev.nelly.hyt.hytprotocol.GermGui;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import javax.imageio.ImageIO;

import me.memorial.events.EventTarget;
import me.memorial.events.impl.misc.PacketEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

@ModuleInfo(name = "Protocol", description = "Protocol", category = ModuleCategory.MISC)
public class Protocol extends Module {
    private String field_3481;
    public static String field_3285;
    public static String field_955;

    @Nullable
    @Override
    public String getTag() {
        return "Hyt";
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S3FPacketCustomPayload) {
            S3FPacketCustomPayload payloadPacket = (S3FPacketCustomPayload)packet;
            switch (payloadPacket.getChannelName()) {
                case "REGISTER":
                    mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("REGISTER", new PacketBuffer(Unpooled.buffer().writeBytes("FML|HS\u0000FML\u0000FML|MP\u0000FML\u0000FORGE\u0000germplugin-netease\u0000hyt0\u0000armourers".getBytes()))));
                    break;
                case "germplugin-netease":
                    PacketBuffer packetBuffer = new PacketBuffer(payloadPacket.getBufferData());
                    if (packetBuffer.readInt() == 73) {
                        this.method_4441(packetBuffer);
                        if (!this.field_3481.equals("gui")) {
                            return;
                        }

                        PacketBuffer packetBuffer2 = new PacketBuffer(Unpooled.buffer());
                        this.method_6786(packetBuffer2);
                        mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", packetBuffer2));
                        Yaml yaml = new Yaml();
                        Map<String, Object> objectMap = (Map)yaml.load(field_955);
                        System.out.println(field_955);
                        if (objectMap != null) {
                            PacketBuffer packetBuffer3 = new PacketBuffer(Unpooled.buffer().writeInt(13));
                            mc.addScheduledTask(() -> {
                                this.getBg(objectMap);
                            });
                            mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", packetBuffer3));
                        }
                    }
            }
        }

        if(packet != null) {
            if (event.getPacket() != null && event.getPacket() instanceof C17PacketCustomPayload) {
                C17PacketCustomPayload packet2 = (C17PacketCustomPayload)event.getPacket();
                String channel = packet2.getChannelName();
                ByteBuf payload = packet2.getBufferData();
                int oldIndex = payload.readerIndex();
                byte[] data = new byte[payload.readableBytes()];
                payload.readBytes(data);
                payload.readerIndex(oldIndex);
                String payloadString = new String(data);
                if (channel.equals("MC|Brand") && payloadString.equals("vanilla")) {
                    payload.clear();
                    payload.writeBytes("fml,forge".getBytes());
                }
            }
        }

    }



    public String getToken(Map<String, Object> objectMap) {
        if (objectMap == null) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            Map<String, Object> newMap = (Map)objectMap.get(field_3285);
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
                    String s3 = (String)var8.next();
                    newMap4 = (Map)scrollableParts.get(s3);
                    sb.append(s3).append("$");
                }

                Map<String, Object> relativeParts = (Map)newMap4.get("relativeParts");
                Iterator var14 = relativeParts.keySet().iterator();
                if (var14.hasNext()) {
                    String s2 = (String)var14.next();
                    sb.append(s2);
                }

                return sb.toString();
            }
        }
    }

    public void getBg(Map<String, Object> objectMap) {
        if (objectMap != null) {
            Map<String, Object> newMap = (Map)objectMap.get(field_3285);
            if (newMap != null) {
                Map<String, Object> bgMap = (Map)newMap.get(field_3285 + "_bg");
                String downloadUrl = (String)bgMap.get("path");
                int width = Integer.parseInt((String)bgMap.get("width"));
                int height = Integer.parseInt((String)bgMap.get("height"));
                Map<String, Object> buttonMap = null;

                Map.Entry stringObjectEntry;
                for(Iterator var8 = newMap.entrySet().iterator(); var8.hasNext(); buttonMap = (Map)stringObjectEntry.getValue()) {
                    stringObjectEntry = (Map.Entry)var8.next();
                }

                Map<String, Object> scrollableParts = null;
                if (buttonMap != null) {
                    scrollableParts = (Map)buttonMap.get("scrollableParts");
                }

                GermGui germGui = new GermGui((float)width, (float)height, scrollableParts);
                mc.displayGuiScreen(germGui);
            }

        }
    }

    private BufferedImage readResource(String url) throws Exception {
        return ImageIO.read(new URL(url));
    }

    public void method_6786(PacketBuffer packetBuffer) {
        packetBuffer.writeInt(4);
        packetBuffer.writeInt(0);
        packetBuffer.writeInt(0);
        packetBuffer.writeString(field_3285);
        packetBuffer.writeString(field_3285);
        packetBuffer.writeString(field_3285);
    }

    public void method_4441(PacketBuffer packetBuffer) {
        this.field_3481 = packetBuffer.readStringFromBuffer(32767);
        field_3285 = packetBuffer.readStringFromBuffer(32767);
        field_955 = packetBuffer.readStringFromBuffer(9999999);
    }
}
