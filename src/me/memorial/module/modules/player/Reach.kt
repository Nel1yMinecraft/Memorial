package me.memorial.module.modules.player

import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.value.FloatValue

@ModuleInfo(name = "Reach", description = "Increases your reach.", category = ModuleCategory.PLAYER)
class Reach : Module() {

    val combatReachValue = FloatValue("CombatReach", 3.5f, 3f, 7f)
    val buildReachValue = FloatValue("BuildReach", 5f, 4.5f, 7f)

    val maxRange: Float
        get() {
            val combatRange = combatReachValue.get()
            val buildRange = buildReachValue.get()

            return if (combatRange > buildRange) combatRange else buildRange
        }
}
