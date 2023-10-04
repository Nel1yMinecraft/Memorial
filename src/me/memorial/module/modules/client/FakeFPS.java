package me.memorial.module.modules.client;

import lombok.Getter;
import me.memorial.events.EventTarget;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.value.IntegerValue;

import java.util.Random;

@ModuleInfo(name = "FakeFPS",description = "FakeFPS" , category = ModuleCategory.CLIENT)
public class FakeFPS extends Module {
    public static final IntegerValue maxfakefps = new IntegerValue("Max-FakeFps", 150, 1, 3000);
    public static final IntegerValue minfakefps = new IntegerValue("Min-FakeFps", 130, 0, 3000);

    public static int getfakefps() {
        Random random = new Random();
        return random.nextInt(maxfakefps.get() - minfakefps.get() + 1) + minfakefps.get();
    }

    @EventTarget
    public void onUpdate() {
        if(minfakefps.get() > maxfakefps.get()) {
            minfakefps.set(maxfakefps.get());
        }
    }
    
    public static boolean state = false;
    @Override
    public void onEnable() {
        state = true;
    }

    @Override
    public void onDisable() {
        state = false;
    }
}
