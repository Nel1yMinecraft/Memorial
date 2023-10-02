/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package me.memorial.ui.client.clickgui.style.styles.dropdown;

import me.memorial.value.Value;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Manager {
    private static final List<Value> settingList = new CopyOnWriteArrayList<>();
    public static void put(Value setting) {
        settingList.add(setting);
    }
    public static List<Value> getSettingList() {
        return settingList;
    }

}
