package me.memorial.module.modules.misc;

import me.memorial.Memorial;
import me.memorial.events.EventTarget;
;
import me.memorial.events.impl.render.Render2DEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.file.configs.FriendsConfig;
import me.memorial.utils.ClientUtils;
import me.memorial.utils.render.ColorUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

@ModuleInfo(name = "MidClick", description = "Allows you to add a player as a friend by right clicking him.", category = ModuleCategory.MISC)
public class MidClick extends Module {

    private boolean wasDown;

    @EventTarget
    public void onRender(Render2DEvent event) {
        if(mc.currentScreen != null)
            return;

        if(!wasDown && Mouse.isButtonDown(2)) {
            final Entity entity = mc.objectMouseOver.entityHit;

            if(entity instanceof EntityPlayer) {
                final String playerName = ColorUtils.stripColor(entity.getName());
                final FriendsConfig friendsConfig = Memorial.fileManager.friendsConfig;

                if(!friendsConfig.isFriend(playerName)) {
                    friendsConfig.addFriend(playerName);
                    Memorial.fileManager.saveConfig(friendsConfig);
                    ClientUtils.displayChatMessage("§a§l" + playerName + "§c was added to your friends.");
                }else{
                    friendsConfig.removeFriend(playerName);
                    Memorial.fileManager.saveConfig(friendsConfig);
                    ClientUtils.displayChatMessage("§a§l" + playerName + "§c was removed from your friends.");
                }
            }else
                ClientUtils.displayChatMessage("§c§lError: §aYou need to select a player.");
        }

        wasDown = Mouse.isButtonDown(2);
    }

}
