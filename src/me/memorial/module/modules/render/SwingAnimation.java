package me.memorial.module.modules.render;

import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;

@ModuleInfo(name = "SwingAnimation", description = "Changes swing animation.", category = ModuleCategory.RENDER)
public class SwingAnimation extends Module {
	private static SwingAnimation instance;

	public static SwingAnimation getInstance() {
		return instance;
	}

	public SwingAnimation(){
		instance = this;
	}
}