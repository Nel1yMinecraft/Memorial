package me.memorial.module.modules.movement;

import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;

@ModuleInfo(name = "NoJumpDelay", description = "Removes delay between jumps.", category = ModuleCategory.MOVEMENT)
public class NoJumpDelay extends Module {
	private static NoJumpDelay instance;

	public NoJumpDelay(){
		instance = this;
	}

	public static NoJumpDelay getInstance(){
		return instance;
	}
}
