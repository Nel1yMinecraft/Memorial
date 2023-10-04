package me.memorial.module.modules.misc;

import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.value.BoolValue;
import net.minecraft.entity.EntityLivingBase;

@ModuleInfo(name = "Teams", description = "Prevents Killaura from attacking team mates.", category = ModuleCategory.MISC)
public class Teams extends Module {

    private BoolValue scoreboardValue = new BoolValue("ScoreboardTeam", true);
    private BoolValue colorValue = new BoolValue("Color", true);
    private BoolValue gommeSWValue = new BoolValue("GommeSW", false);

    /**
     * Check if [entity] is in your own team using scoreboard, name color or team prefix
     */
    public boolean isInYourTeam(EntityLivingBase entity) {
        if (mc.thePlayer == null)
            return false;

        if (scoreboardValue.get() && mc.thePlayer.getTeam() != null && entity.getTeam() != null &&
                mc.thePlayer.getTeam().isSameTeam(entity.getTeam()))
            return true;

        if (gommeSWValue.get() && mc.thePlayer.getDisplayName() != null && entity.getDisplayName() != null) {
            String targetName = entity.getDisplayName().getFormattedText().replace("§r", "");
            String clientName = mc.thePlayer.getDisplayName().getFormattedText().replace("§r", "");
            if (targetName.startsWith("T") && clientName.startsWith("T"))
                if (Character.isDigit(targetName.charAt(1)) && Character.isDigit(clientName.charAt(1)))
                    return targetName.charAt(1) == clientName.charAt(1);
        }

        if (colorValue.get() && mc.thePlayer.getDisplayName() != null && entity.getDisplayName() != null) {
            String targetName = entity.getDisplayName().getFormattedText().replace("§r", "");
            String clientName = mc.thePlayer.getDisplayName().getFormattedText().replace("§r", "");
            return targetName.startsWith("§" + clientName.charAt(1));
        }

        return false;
    }

}
