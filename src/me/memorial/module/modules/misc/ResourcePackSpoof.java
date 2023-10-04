package me.memorial.module.modules.misc;

import me.memorial.events.EventTarget;
import me.memorial.events.impl.misc.PacketEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.ClientUtils;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S48PacketResourcePackSend;

import java.net.URI;
import java.net.URISyntaxException;

@ModuleInfo(name = "ResourcePackSpoof", description = "Prevents servers from forcing you to download their resource pack.", category = ModuleCategory.MISC)
public class ResourcePackSpoof extends Module {

    @EventTarget
    public void onPacket(PacketEvent event) {
        Object packet = event.getPacket();

        if (packet instanceof S48PacketResourcePackSend) {
            String url = ((S48PacketResourcePackSend) packet).getURL();
            String hash = ((S48PacketResourcePackSend) packet).getHash();

            try {
                String scheme = new URI(url).getScheme();
                boolean isLevelProtocol = "level".equals(scheme);

                if (!"http".equals(scheme) && !"https".equals(scheme) && !isLevelProtocol) {
                    throw new URISyntaxException(url, "Wrong protocol");
                }

                if (isLevelProtocol && (url.contains("") || !url.endsWith("/resources.zip"))) {
                    throw new URISyntaxException(url, "Invalid levelstorage resourcepack path");
                }

                mc.getNetHandler().addToSendQueue(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.ACCEPTED));
                mc.getNetHandler().addToSendQueue(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
            } catch (URISyntaxException e) {
                ClientUtils.getLogger().error("Failed to handle resource pack", e);
                mc.getNetHandler().addToSendQueue(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            }
        }
    }

}
