package me.memorial.module.modules.world;

import me.memorial.Memorial;
import me.memorial.events.EventTarget;
import me.memorial.events.impl.player.UpdateEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.value.FloatValue;

@ModuleInfo(name = "FastBreak", description = "Allows you to break blocks faster.", category = ModuleCategory.WORLD)
public class FastBreak extends Module {

    private FloatValue breakDamage = new FloatValue("BreakDamage", 0.8F, 0.1F, 1F);

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        mc.playerController.blockHitDelay = 0;
        final Fucker Fucker = (Fucker) Memorial.moduleManager.getModule(Fucker.class);

        if (mc.playerController.curBlockDamageMP > breakDamage.get())
            mc.playerController.curBlockDamageMP = 1F;

        if (Fucker.getCurrentDamage() > breakDamage.get())
            Fucker.setCurrentDamage(1F);

        if (Nuker.currentDamage > breakDamage.get())
            Nuker.currentDamage = 1F;
    }
}
