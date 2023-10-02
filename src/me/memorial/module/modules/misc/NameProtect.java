package me.memorial.module.modules.misc;

import me.memorial.Memorial;
import me.memorial.events.EventTarget;
import me.memorial.events.TextEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.file.configs.FriendsConfig;
import me.memorial.utils.misc.StringUtils;
import me.memorial.utils.render.ColorUtils;
import me.memorial.value.BoolValue;
import me.memorial.value.TextValue;
import net.minecraft.client.network.NetworkPlayerInfo;

@ModuleInfo(name = "NameProtect", description = "Changes playernames clientside.", category = ModuleCategory.MISC)
public class NameProtect extends Module {
    private static NameProtect instance;

    public NameProtect(){
        instance = this;
    }

    public static NameProtect getInstance(){
        return instance;
    }

    private final TextValue fakeNameValue = new TextValue("FakeName", "&cMe");
    public final BoolValue allPlayersValue = new BoolValue("AllPlayers", false);
    public final BoolValue skinProtectValue = new BoolValue("SkinProtect", true);

    @EventTarget(ignoreCondition = true)
    public void onText(final TextEvent event) {
        if(mc.thePlayer == null || event.getText().contains("§8[§9§l" + Memorial.CLIENT_NAME + "§8] §3"))
            return;

        for (final FriendsConfig.Friend friend : Memorial.fileManager.friendsConfig.getFriends())
            event.setText(StringUtils.replace(event.getText(), friend.getPlayerName(), ColorUtils.translateAlternateColorCodes(friend.getAlias()) + "§f"));

        if(!getState())
            return;

        event.setText(StringUtils.replace(event.getText(), mc.thePlayer.getName(), ColorUtils.translateAlternateColorCodes(fakeNameValue.get()) + "§f"));

        if(allPlayersValue.get())
            for(final NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap())
                event.setText(StringUtils.replace(event.getText(), playerInfo.getGameProfile().getName(), "Protected User"));
    }

}
