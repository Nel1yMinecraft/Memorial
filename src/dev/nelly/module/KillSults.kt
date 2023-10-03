package dev.nelly.module

import me.memorial.Memorial
import me.memorial.events.EventTarget
import me.memorial.events.impl.player.UpdateEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.module.modules.combat.KillAura
import me.memorial.ui.client.hud.element.elements.Notification
import me.memorial.value.BoolValue

@ModuleInfo("KillSults","KillSults",ModuleCategory.MISC)
class KillSults : Module() {
    val addnotif = BoolValue("AddNotification",true)
    var kill = 0
    val killaura = Memorial.moduleManager.getModule(KillAura::class.java) as KillAura
    val target = killaura.target!!
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (target.health <= 0) {
            kill += 1
            l()
        }
    }

    fun l() {
        if(addnotif.get()) {
            Memorial.hud.notifications.add(Notification("Kills +1"))
        }
    }

    override val tag: String
        get() = kill.toString()
}