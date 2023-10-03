package me.memorial.module.modules.movement

import me.memorial.events.EventTarget
import me.memorial.events.impl.move.MoveEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.value.BoolValue

@ModuleInfo(name = "SafeWalk", description = "Prevents you from falling down as if you were sneaking.", category = ModuleCategory.MOVEMENT)
class SafeWalk : Module() {

    private val airSafeValue = BoolValue("AirSafe", false)

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (airSafeValue.get() || mc.thePlayer.onGround)
            event.isSafeWalk = true
    }
}
