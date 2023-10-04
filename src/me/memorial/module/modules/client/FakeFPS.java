package me.memorial.module.modules.client;

import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.value.IntegerValue;

@ModuleInfo(name = "FakeFPS",description = "FakeFPS" , category = ModuleCategory.CLIENT)
public class FakeFPS extends Module {
    public final IntegerValue fakefps = new IntegerValue("FakeFps", 130, 10, 3000);
}
