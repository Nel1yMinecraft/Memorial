package dev.nelly.module

import dev.nelly.viamcp.utils.AttackOrder
import me.memorial.Memorial
import me.memorial.events.EventTarget
import me.memorial.events.impl.player.AttackEvent
import me.memorial.events.impl.player.UpdateEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.module.modules.misc.AntiBot
import me.memorial.module.modules.misc.Teams
import me.memorial.utils.EntityUtils
import me.memorial.utils.Rotation
import me.memorial.utils.RotationUtils
import me.memorial.utils.extensions.getDistanceToEntityBox
import me.memorial.value.BoolValue
import me.memorial.value.DoubleValue
import me.memorial.value.FloatValue
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

@ModuleInfo("KillAura2","Nel1y",ModuleCategory.COMBAT)
class KillAura2 : Module() {

    //攻击距离
    val rangeValue = DoubleValue("Range",3.0,8.0,0.1)

    //挥手
    private val swingValue = BoolValue("Swing",true)

    //静默转头
    private val silentrotationValue = BoolValue("SilentRotation",true)

    //最大转头速度
    private val maxTurnSpeed: FloatValue = object : FloatValue("MaxTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minTurnSpeed.get()
            if (v > newValue) set(v)
        }
    }

    //最小转头速度
    private val minTurnSpeed: FloatValue = object : FloatValue("MinTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxTurnSpeed.get()
            if (v < newValue) set(v)
        }
    }


    var target: EntityLivingBase? = null
    var click = 0
    var blocking = false

    @EventTarget
    fun onUpdate(event : UpdateEvent) {

        /*
        if (target!!.getDistanceToEntity(mc.thePlayer) <= rangeValue.get()) {
            setRotation(
                Rotation(
                    (RotationUtils.getAngles(target).yaw + Math.random() * 4f - 4f / 2).toFloat(),
                    (RotationUtils.getAngles(target).pitch + Math.random() * 4f - 4f / 2).toFloat()
                )
            )
            attackEntity()
        }

         */


        for (entity in mc.theWorld.loadedEntityList) {

            if (entity is EntityLivingBase && isEnemy(entity) && mc.thePlayer.getDistanceToEntityBox(entity) <= rangeValue.get()) {
                setRotation(
                    Rotation(
                        (RotationUtils.getAngles(entity).yaw + Math.random() * 4f - 4f / 2).toFloat(),
                        (RotationUtils.getAngles(entity).pitch + Math.random() * 4f - 4f / 2).toFloat()
                    )
                )
                attackEntity(entity)

            }
        }

    }




    //  队伍判断以及实体类型判断
    private fun isEnemy(entity: Entity?): Boolean {
        if (entity is EntityLivingBase && entity.isEntityAlive && entity.health > 0 && entity != mc.thePlayer) {
            if (!EntityUtils.targetInvisible && entity.isInvisible())
                return false

            if (EntityUtils.targetPlayer && entity is EntityPlayer) {
                if (entity.isSpectator || AntiBot.isBot(entity))
                    return false

                val teams = Memorial.moduleManager[Teams::class.java] as Teams

                return !teams.state || !teams.isInYourTeam(entity)
            }

            return EntityUtils.targetMobs && EntityUtils.isMob(entity) || EntityUtils.targetAnimals &&
                    EntityUtils.isAnimal(entity)
        }

        return false
    }


    //  攻击
    fun attackEntity(entity : Entity?,swing : Boolean = swingValue.get()) {

        // 防止连续发送攻击包
        if(mc.thePlayer.attackingEntity != null) {
            return
        }

        // 停止防砍
        if (mc.thePlayer.isBlocking || blocking) {
            mc.netHandler.addToSendQueue(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                BlockPos.ORIGIN, EnumFacing.DOWN)
            )
            blocking = false
        }

        // 注册AttackEvent
        Memorial.eventManager.callEvent(AttackEvent(entity))

        if(swing) {
            mc.thePlayer.swingItem()
            click += 1
        }

        AttackOrder.sendFixedAttack(mc.thePlayer,entity)

    }


    //  转头
    fun setRotation(rotation: Rotation) {
        val limitedRotation = RotationUtils.limitAngleChange(
            RotationUtils.serverRotation, rotation,
            (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
        )

        // 转头逻辑
        if (silentrotationValue.get()) {
            RotationUtils.setTargetRotation(limitedRotation, 0)
            mc.thePlayer.renderYawOffset = RotationUtils.targetRotation.yaw
        } else {
            limitedRotation.toPlayer(mc.thePlayer)
        }


    }


}