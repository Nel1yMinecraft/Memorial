package me.memorial.module.modules.client

import me.memorial.Memorial
import me.memorial.events.EventTarget
import me.memorial.events.PacketEvent
import me.memorial.events.impl.render.Render3DEvent

import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.module.modules.combat.KillAura
import me.memorial.module.modules.world.*
import me.memorial.utils.RotationUtils
import me.memorial.value.BoolValue
import net.minecraft.network.play.client.C03PacketPlayer

@ModuleInfo(name = "Rotations", description = "Allows you to see server-sided head and body rotations.", category = ModuleCategory.CLIENT)
class Rotations : Module() {

    init {
        state = true
    }

    private val bodyValue = BoolValue("Body", true)

    private var playerYaw: Float? = null

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (RotationUtils.serverRotation != null && !bodyValue.get())
            mc.thePlayer.rotationYawHead = RotationUtils.serverRotation.yaw
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (!bodyValue.get() || !shouldRotate() || mc.thePlayer == null)
            return

        val packet = event.packet
        if (packet is C03PacketPlayer.C06PacketPlayerPosLook || packet is C03PacketPlayer.C05PacketPlayerLook) {
            playerYaw = (packet as C03PacketPlayer).yaw
            mc.thePlayer.renderYawOffset = packet.getYaw()
            mc.thePlayer.rotationYawHead = packet.getYaw()
        } else {
            if (playerYaw != null)
                mc.thePlayer.renderYawOffset = this.playerYaw!!
            mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset
        }
    }

    private fun getState(module: Class<*>) = Memorial.moduleManager[module]!!.state

    private fun shouldRotate(): Boolean {
        val killAura = Memorial.moduleManager.getModule(KillAura::class.java) as KillAura
        return getState(Scaffold::class.java) || getState(Tower::class.java) ||
                (getState(KillAura::class.java) && killAura.target != null) || getState(ChestAura::class.java)
                getState(Fucker::class.java) || getState(CivBreak::class.java) || getState(Nuker::class.java)
        }
    }

