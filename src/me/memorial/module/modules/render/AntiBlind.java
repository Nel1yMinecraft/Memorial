package me.memorial.module.modules.render;

import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.value.BoolValue;
import me.memorial.value.FloatValue;

@ModuleInfo(name = "AntiBlind", description = "Cancels blindness effects.", category = ModuleCategory.RENDER)
public class AntiBlind extends Module {
	private static AntiBlind instance;

	public static AntiBlind getInstance(){
		return instance;
	}

	public AntiBlind(){
		instance = this;
	}


	public BoolValue confusionEffect = new BoolValue("Confusion", true);
	public BoolValue pumpkinEffect = new BoolValue("Pumpkin", true);
	public FloatValue fireEffect = new FloatValue("FireOpacity", 0.5f, 0, 1);
}
