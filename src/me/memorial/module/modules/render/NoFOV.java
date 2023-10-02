package me.memorial.module.modules.render;

import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.value.FloatValue;

@ModuleInfo(name = "NoFOV", description = "Disables FOV changes caused by speed effect, etc.", category = ModuleCategory.RENDER)
public class NoFOV extends Module {
	private static NoFOV instance;

	public static NoFOV getInstance(){
		return instance;
	}

    public NoFOV() {
        instance = this;
    }
	public FloatValue fovValue = new FloatValue("FOV", 1f, 0f, 1.5f);
}
