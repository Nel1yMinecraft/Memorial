package dev.nelly.module

import me.memorial.Memorial
import me.memorial.events.EventTarget
import me.memorial.events.impl.player.UpdateEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.module.modules.movement.Strafe
import me.memorial.value.BoolValue
import me.memorial.value.FloatValue
import me.memorial.value.IntegerValue
import net.minecraft.entity.EntityLivingBase

@ModuleInfo(name = "EntitySpeed", description = "", category = ModuleCategory.MOVEMENT)
class EntitySpeed : Module() {
    private val onlyAir = BoolValue("OnlyAir", false)
    private val okstrafe = BoolValue("Strafe", false)
    private val keepSprint = BoolValue("KeepSprint", true)
    private val speedUp = BoolValue("SpeedUp", false)
    private val speed = IntegerValue("Speed", 0, 0, 10)
    private val distance = FloatValue("Range", 0f, 0f, 1f)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val strafe = Memorial.moduleManager.getModule(Strafe::class.java) as Strafe
        for (entity in mc.theWorld.loadedEntityList) {
            if (entity is EntityLivingBase && entity.entityId != mc.thePlayer.entityId && mc.thePlayer.getDistanceToEntity(
                    entity
                ) <= distance.get() && (!onlyAir.get() || !mc.thePlayer.onGround)
            ) {
                if (speedUp.get()) {
                    mc.thePlayer.motionX *= (1 + (speed.get() * 0.01))
                    mc.thePlayer.motionZ *= (1 + (speed.get() * 0.01))
                }
                if (keepSprint.get()) {
                    mc.thePlayer.isSprinting = true
                }
                if (okstrafe.get()) {
                    strafe.state = true
                }
                return
            }
            mc.thePlayer.isSprinting = false
            strafe.state = false
        }
    }

}