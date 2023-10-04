package me.memorial.module.modules.combat;

import me.memorial.Memorial;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.value.FloatValue;

@ModuleInfo(name = "HitBox", description = "Makes hitboxes of targets bigger.", category = ModuleCategory.COMBAT)
public class HitBox extends Module {
    public static HitBox instance;

    public HitBox() {
        instance = this;
    }

    public FloatValue sizeValue = new FloatValue("Size", 0.4F, 0F, 1F);
}
