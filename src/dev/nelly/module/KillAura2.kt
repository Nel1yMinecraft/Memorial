package dev.nelly.module

import dev.nelly.viamcp.utils.AttackOrder
import me.memorial.Memorial
import me.memorial.events.EventTarget
import me.memorial.events.impl.move.StrafeEvent
import me.memorial.events.impl.player.AttackEvent
import me.memorial.events.impl.player.UpdateEvent
import me.memorial.events.impl.render.Render3DEvent
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
import me.memorial.value.IntegerValue
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import org.lwjgl.opengl.GL11
import kotlin.math.cos
import kotlin.math.sin


@ModuleInfo("KillAura2","Nel1y",ModuleCategory.COMBAT)
class KillAura2 : Module() {

    //攻击距离
    val rangeValue = DoubleValue("Range", 3.0, 0.1, 8.0)

    //攻击是否穿墙
    private val throughWalls = BoolValue("ThroughWalls", true)

    //挥手
    private val swingValue = BoolValue("Swing", true)

    //静默转头
    private val silentrotationValue = BoolValue("SilentRotation", true)

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

    //移动修复
    private val movefixValue = BoolValue("MoveFix", true)

    //距离光环显示
    private val circleValue = BoolValue("Circle", true)
    private val circleRed = IntegerValue("CircleRed", 255, 0, 255)
    private val circleGreen = IntegerValue("CircleGreen", 255, 0, 255)
    private val circleBlue = IntegerValue("CircleBlue", 255, 0, 255)
    private val circleAlpha = IntegerValue("CircleAlpha", 255, 0, 255)
    private val circleAccuracy = IntegerValue("CircleAccuracy", 15, 0, 60)


    var target: EntityLivingBase? = null
    var click = 0
    var blocking = false

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (movefixValue.get() && target != null) {
            RotationUtils.targetRotation.applyStrafeToPlayer(event)
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {

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


        mc.theWorld.loadedEntityList
            .asSequence()
            .filterIsInstance<EntityLivingBase>()
            .filter { isEnemy(it) && mc.thePlayer.getDistanceToEntityBox(it) <= rangeValue.get() }
            .forEach {
                setRotation(it)
                attackEntity(it)
                target = it
            }

        if (target == null) {
            stopBlocking()
            click = 0
        }

    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {

        if (circleValue.get()) {
            GL11.glPushMatrix()
            GL11.glTranslated(
                mc.thePlayer!!.lastTickPosX + (mc.thePlayer!!.posX - mc.thePlayer!!.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX,
                mc.thePlayer!!.lastTickPosY + (mc.thePlayer!!.posY - mc.thePlayer!!.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY,
                mc.thePlayer!!.lastTickPosZ + (mc.thePlayer!!.posZ - mc.thePlayer!!.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ
            )
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

            GL11.glLineWidth(1F)
            GL11.glColor4f(
                circleRed.get().toFloat() / 255.0F,
                circleGreen.get().toFloat() / 255.0F,
                circleBlue.get().toFloat() / 255.0F,
                circleAlpha.get().toFloat() / 255.0F
            )
            GL11.glRotatef(90F, 1F, 0F, 0F)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            for (i in 0..360 step 61 - circleAccuracy.get()) { // You can change circle accuracy  (60 - accuracy)
                GL11.glVertex2f(
                    (cos(i * Math.PI / 180.0).toFloat() * rangeValue.get()).toFloat(),
                    ((sin(i * Math.PI / 180.0).toFloat() * rangeValue.get()).toFloat())
                )
            }
            GL11.glVertex2f(
                (cos(360 * Math.PI / 180.0).toFloat() * rangeValue.get()).toFloat(),
                ((sin(360 * Math.PI / 180.0).toFloat() * rangeValue.get()).toFloat())
            )

            GL11.glEnd()

            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)

            GL11.glPopMatrix()
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
    private fun attackEntity(entity: Entity?, swing: Boolean = swingValue.get()) {

        // 防止连续发送攻击包
        if (mc.thePlayer.attackingEntity != null) {
            return
        }

        // 停止防砍
        stopBlocking()

        // 注册AttackEvent
        Memorial.eventManager.callEvent(AttackEvent(entity))

        if (swing) {
            mc.thePlayer.swingItem()
            ++click
        }

        AttackOrder.sendFixedAttack(mc.thePlayer, entity)

    }


    //  转头
    fun setRotation(entity: Entity) {

        val (_, rotation) = RotationUtils.lockView(
            entity.entityBoundingBox,
            false,
            false,
            false,
            throughWalls.get(),
            rangeValue.get().toFloat()
        )

        val limitedRotation = RotationUtils.limitAngleChange(
            RotationUtils.serverRotation,
            rotation,
            (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
        )

        // 转头逻辑
        if (silentrotationValue.get()) {
            RotationUtils.setTargetRotation(limitedRotation, 0)
            mc.thePlayer.renderYawOffset = RotationUtils.targetRotation.yaw
        } else {
            limitedRotation.toPlayer(mc.thePlayer!!)


        }

    }

    fun stopBlocking() {

        if (mc.thePlayer.isBlocking || blocking) {
            mc.netHandler.addToSendQueue(
                C07PacketPlayerDigging(
                    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN, EnumFacing.DOWN
                )
            )
            blocking = false
        }

    }


    override val tag: String
        get() = "${rangeValue.get()}-$click"
}
