package me.memorial.module.modules.render;

import me.memorial.events.EventTarget;
import me.memorial.events.UpdateEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;

@ModuleInfo(name = "NoBob", description = "Disables the view bobbing effect.", category = ModuleCategory.RENDER)
public class NoBob extends Module {

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        mc.thePlayer.distanceWalkedModified = 0f;
    }
}
