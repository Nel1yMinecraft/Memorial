package me.memorial.module.modules.render;

import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;

@ModuleInfo(name = "CameraClip", description = "Allows you to see through walls in third person view.", category = ModuleCategory.RENDER)
public class CameraClip extends Module {
	private static CameraClip instance;

	public static CameraClip getInstance(){
		return instance;
	}

	public CameraClip(){
		instance = this;
	}
}