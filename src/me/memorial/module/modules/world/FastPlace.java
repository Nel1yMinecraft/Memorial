package me.memorial.module.modules.world;

import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.value.IntegerValue;

@ModuleInfo(name = "FastPlace", description = "Allows you to place blocks faster.", category = ModuleCategory.WORLD)
public class FastPlace extends Module {

    public IntegerValue speedValue = new IntegerValue("Speed", 0, 0, 4);

    public static FastPlace instance;

    public FastPlace() {
        instance = this;
    }
}
