package dev.nelly.module

import me.memorial.Memorial
import me.memorial.events.EventTarget
import me.memorial.events.impl.player.UpdateEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.module.modules.combat.KillAura
import me.memorial.value.BoolValue

@ModuleInfo("KillSults","KillSults",ModuleCategory.MISC)
class KillSults : Module() {
    var kill = 0
    val killaura = Memorial.moduleManager.getModule(KillAura::class.java) as KillAura
    val target = killaura.target!!
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (target.health <= 0) {
            ++kill
        }
    }


    override val tag: String
        get() = kill.toString()
}