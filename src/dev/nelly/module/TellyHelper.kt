package dev.nelly.module

import me.memorial.Memorial
import me.memorial.events.EventTarget
import me.memorial.events.impl.move.MotionEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.module.modules.world.ScaFull
import me.memorial.module.modules.world.Scaffold
import me.memorial.utils.MovementUtils
import me.memorial.value.BoolValue
import me.memorial.value.ListValue

@ModuleInfo("TellyHelper","Test",ModuleCategory.WORLD)
class TellyHelper : Module() {

    val mode = ListValue("Mode", arrayOf("Scaffold","ScaFull"),"ScaFull")
    val autojump = BoolValue("AutoJump",true)

    @EventTarget
    fun onMotion(event : MotionEvent) {

        if(autojump.get() && MovementUtils.isMoving() && mc.thePlayer.onGround && !mc.thePlayer.isSneaking && !mc.gameSettings.keyBindSneak.isKeyDown && !mc.gameSettings.keyBindJump.isKeyDown) {
            mc.thePlayer.jump()
        }

        setscaffold(!mc.thePlayer.onGround)

    }

    override fun onDisable() {
        Memorial.moduleManager.getModule(Scaffold::class.java)!!.state = false
        Memorial.moduleManager.getModule(ScaFull::class.java)!!.state = false
        super.onDisable()
    }


    fun setscaffold(state : Boolean) {
        if(mode.get().equals("ScaFull")) {
            Memorial.moduleManager.getModule(ScaFull::class.java)!!.state = state
        }

        if(mode.get().equals("Scaffold")) {
            Memorial.moduleManager.getModule(Scaffold::class.java)!!.state = state
        }

    }
}