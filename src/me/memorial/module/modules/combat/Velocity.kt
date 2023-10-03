package me.memorial.module.modules.combat

import me.memorial.Memorial
import me.memorial.events.*
import me.memorial.events.impl.player.MotionEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.module.modules.player.Blink
import me.memorial.value.BoolValue
import me.memorial.value.FloatValue
import me.memorial.value.IntegerValue

import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

@ModuleInfo(name = "Velocity", description = "By Kid .", category = ModuleCategory.COMBAT)
class Velocity : Module() {

    private val airticks = IntegerValue("AirTicks",2,0,10)
    private val timerSpeed = FloatValue("TimerSpeed",0.49f,0.1f,1.0f)
    private val airTest = BoolValue("AirTest",true)
    private val jumpFix = BoolValue("JumpFix",true)

    private var airtick = 0
    private var air = false
    private var pre = false
    private var stop = false

    override fun onEnable() {
        airtick = 0
        air = false
        air = false
        pre = false

    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val blink = Memorial.moduleManager.getModule(Blink::class.java) as Blink
        val packet = event.packet
        if(packet is S12PacketEntityVelocity && packet.entityID == mc.thePlayer.entityId && airTest.get()){
            event.cancelEvent()
            airtick = airticks.get()
        }
        if(airtick > 0){
            mc.timer.timerSpeed = timerSpeed.get()
            if(packet is C04PacketPlayerPosition || packet is C06PacketPlayerPosLook){

                mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                    BlockPos(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ),EnumFacing.UP
                ))
                mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                    BlockPos(mc.thePlayer.posX,mc.thePlayer.posY + 1,mc.thePlayer.posZ),EnumFacing.UP
                ))

            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){

        if(airtick > 0){
            air = true

            airtick--
        }

        if(air){
            if(airtick < 1){
                mc.timer.timerSpeed = 1f
                air = false
            }
        }
    }

    @EventTarget
    fun onTIck(event: TickEvent){
        if(airtick > 0){
            mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                BlockPos(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ),EnumFacing.UP
            ))
        }
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent){

    }

    @EventTarget
    fun onMotion(event: MotionEvent){
        pre = !event.state.stateName.equals("Post",true)
    }

    @EventTarget
    fun onJUmp(event: JumpEvent){
        if(airtick > 0){
            if(jumpFix.get())
                event.cancelEvent()
        }
    }
    override val tag: String?
        get() = "Grim"
}