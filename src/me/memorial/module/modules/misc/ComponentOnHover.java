package me.memorial.module.modules.misc;

import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;

@ModuleInfo(name = "ComponentOnHover", description = "Allows you to see onclick action and value of chat message components when hovered.", category = ModuleCategory.MISC)
public class ComponentOnHover extends Module {
	private static ComponentOnHover instance;

	public static ComponentOnHover getInstance(){
		return instance;
	}

	public ComponentOnHover(){
		instance = this;
	}
}