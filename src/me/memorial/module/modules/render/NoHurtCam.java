package me.memorial.module.modules.render;

import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;

@ModuleInfo(name = "NoHurtCam", description = "Disables hurt cam effect when getting hurt.", category = ModuleCategory.RENDER)
public class NoHurtCam extends Module {
	private static NoHurtCam instance;

	public static NoHurtCam getInstance(){
		return instance;
	}

	public NoHurtCam(){
		instance = this;
	}
}
