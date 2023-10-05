package me.memorial.module.modules.render;

import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.value.BoolValue;
import me.memorial.value.FloatValue;

@ModuleInfo(name = "PlayerEdit", description = "Edit the player.", category = ModuleCategory.PLAYER)
public class PlayerEdit extends Module {
    public static FloatValue playerSizeValue = new FloatValue("PlayerSize", 0.5f,0.01f,5f);
    public static BoolValue editPlayerSizeValue = new BoolValue("EditPlayerSize", true);
}
