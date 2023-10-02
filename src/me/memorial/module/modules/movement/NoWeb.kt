package me.memorial.module.modules.movement

import me.memorial.events.EventTarget
import me.memorial.events.UpdateEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.value.ListValue

@ModuleInfo(name = "NoWeb", description = "Prevents you from getting slowed down in webs.", category = ModuleCategory.MOVEMENT)
class NoWeb : Module() {

    private val modeValue = ListValue("Mode", arrayOf("None", "AAC", "LAAC", "Rewi"), "None")

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!mc.thePlayer.isInWeb)
            return

        when (modeValue.get().toLowerCase()) {
            "none" -> mc.thePlayer.isInWeb = false
            "aac" -> {
                mc.thePlayer.jumpMovementFactor = 0.59f

                if (!mc.gameSettings.keyBindSneak.isKeyDown)
                    mc.thePlayer.motionY = 0.0
            }
            "laac" -> {
                mc.thePlayer.jumpMovementFactor = if (mc.thePlayer.movementInput.moveStrafe != 0f) 1.0f else 1.21f

                if (!mc.gameSettings.keyBindSneak.isKeyDown)
                    mc.thePlayer.motionY = 0.0

                if (mc.thePlayer.onGround)
                    mc.thePlayer.jump()
            }
            "rewi" -> {
                mc.thePlayer.jumpMovementFactor = 0.42f

                if (mc.thePlayer.onGround)
                    mc.thePlayer.jump()
            }
        }
    }

    override val tag: String?
        get() = modeValue.get()
}
