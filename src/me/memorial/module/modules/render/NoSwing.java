package me.memorial.module.modules.render;

import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.value.BoolValue;

@ModuleInfo(name = "NoSwing", description = "Disabled swing effect when hitting an entity/mining a block.", category = ModuleCategory.RENDER)
public class NoSwing extends Module {
	private static NoSwing instance;

	public static NoSwing getInstance(){
		return instance;
	}

	public NoSwing(){
		instance = this;
	}
	public final BoolValue serverSideValue = new BoolValue("ServerSide", true);
}