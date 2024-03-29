package me.memorial.module.modules.movement

import me.memorial.events.EventState
import me.memorial.events.EventTarget
import me.memorial.events.impl.move.MotionEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.utils.MovementUtils

@ModuleInfo(name = "Strafe", description = "Allows you to freely move in mid air.", category = ModuleCategory.MOVEMENT)
class Strafe : Module() {

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.state == EventState.POST)
            return

        MovementUtils.strafe()
    }
}
