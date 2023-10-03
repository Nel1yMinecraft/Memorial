package me.memorial.module.modules.player

import me.memorial.Memorial
import me.memorial.events.EventTarget
import me.memorial.events.impl.player.UpdateEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.module.modules.exploit.Ghost
import me.memorial.value.BoolValue
import net.minecraft.client.gui.GuiGameOver

@ModuleInfo(name = "AutoRespawn", description = "Automatically respawns you after dying.", category = ModuleCategory.PLAYER)
class AutoRespawn : Module() {

    private val instantValue = BoolValue("Instant", true)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (Memorial.moduleManager[Ghost::class.java]!!.state)
            return

        if (if (instantValue.get()) mc.thePlayer.health == 0F || mc.thePlayer.isDead else mc.currentScreen is GuiGameOver
                        && (mc.currentScreen as GuiGameOver).enableButtonsTimer >= 20) {
            mc.thePlayer.respawnPlayer()
            mc.displayGuiScreen(null)
        }
    }
}