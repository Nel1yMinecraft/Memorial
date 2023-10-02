package me.memorial.module.modules.render;

import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.value.BoolValue;

@ModuleInfo(name = "TrueSight", description = "Allows you to see invisible entities and barriers.", category = ModuleCategory.RENDER)
public class TrueSight extends Module {
	private static TrueSight instance;

	public static TrueSight getInstance(){
		return instance;
	}

	public TrueSight(){
		instance = this;
	}

    public BoolValue barriersValue = new BoolValue("Barriers", true);
	public BoolValue entitiesValue = new BoolValue("Entities", true);
}