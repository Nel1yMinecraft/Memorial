package dev.nelly.sk1d.module

import me.memorial.Memorial
import me.memorial.events.EventTarget
import me.memorial.events.StrafeEvent
import me.memorial.events.UpdateEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.utils.*
import me.memorial.utils.extensions.getDistanceToEntityBox
import me.memorial.value.BoolValue
import me.memorial.value.FloatValue
import me.memorial.value.IntegerValue
import me.memorial.value.ListValue
import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.Keyboard


@ModuleInfo(name = "AutoSkyWars", category = ModuleCategory.MOVEMENT, description = "space.bilibili.com/500398541")
class AutoSkyWars : Module() {

    private val modeValue = ListValue("mode", arrayOf(
        "firework",
        "fly",
        "flyaim",
        "teleport"
    ), "flyaim")
    private val X = IntegerValue("posX", 0, -100, 100)
    private val Y = IntegerValue("posY", 10, 0, 50)
    private val Z = IntegerValue("posZ", 0, -100, 100)
    private var SilentNoChat = BoolValue("SilentNoChat",false)
    private val vanillaSpeedValue = FloatValue("FlySpeed", 4f, 0f, 10f)
    private val Times = IntegerValue("TpTicks", 10, 0, 100)
    private var TPtimes = Times.get()
    private val rangeValue = FloatValue("AimRange", 114514F, 1F, 114514F)
    private val centerValue = BoolValue("Center", false)
    private val lockValue = BoolValue("Lock", true)
    private val turnSpeedValue = FloatValue("TurnSpeed", 360F, 360F, 114514F)
    private val height = IntegerValue("Teleportheight", 190, 0, 256)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val autoSkyWars = Memorial.moduleManager.getModule(AutoSkyWars::class.java) as AutoSkyWars
        if (autoSkyWars.state){
            val vanillaSpeed = vanillaSpeedValue.get()
            val thePlayer = mc.thePlayer!!
            if (thePlayer.capabilities.isFlying) {
                when (modeValue.get().toLowerCase()) {
                    "fly","flyaim","firework" -> {//飞行
                        //Silent Teleport ↓
                        if(TPtimes != 0){
                            if (!SilentNoChat.get()) {
                                ClientUtils.displayChatMessage("[修改空岛出生点]芜湖!起飞！！")
                            }
                            thePlayer.setPositionAndRotation(
                                thePlayer.posX + X.get(),
                                thePlayer.posY + Y.get(),
                                thePlayer.posZ + Z.get(),
                                thePlayer.rotationYaw,
                                thePlayer.rotationPitch
                            )
                            TPtimes -= 1
                            when (modeValue.get().toLowerCase()) {
                                "firework" -> {
                                    thePlayer.setPositionAndRotation(
                                        thePlayer.posX + X.get(),
                                        thePlayer.posY + Y.get(),
                                        thePlayer.posZ + Z.get(),
                                        thePlayer.rotationYaw,
                                        thePlayer.rotationPitch
                                    )
                                }
                            }
                        }
                        when (modeValue.get().toLowerCase()) {
                            "fly","flyaim" -> {
                                thePlayer.motionY = 0.0
                                thePlayer.motionX = 0.0
                                thePlayer.motionZ = 0.0
                                if (mc.gameSettings.keyBindJump.isKeyDown) thePlayer.motionY += vanillaSpeed
                                if (mc.gameSettings.keyBindSneak.isKeyDown) thePlayer.motionY -= vanillaSpeed
                                MovementUtils.strafe(vanillaSpeed)
                                when (modeValue.get().toLowerCase()) {
                                    "flyaim" -> {
                                        mc.gameSettings.keyBindForward.pressed = true
                                    }
                                }
                            }
                        }
                    }
                    "teleport" -> {
                        if(TPtimes != 0){
                            thePlayer.setPositionAndRotation(
                                thePlayer.posX + X.get(),
                                thePlayer.posY + Y.get(),
                                thePlayer.posZ + Z.get(),
                                thePlayer.rotationYaw,
                                thePlayer.rotationPitch
                            )
                            TPtimes -= 1
                        }else{
                            val PlayerX = thePlayer.posX
                            val PlayerZ = thePlayer.posZ
                            val tpxtimes = PlayerX / 5
                            val tpztimes = PlayerZ / 5
                            val remainZ = tpztimes / 20
                            val remainX = tpxtimes / 20
                            val remainY = thePlayer.posY / 20
                            ClientUtils.displayChatMessage(">>>>>>>>>>>>>>>>>>SkyWars Helper<<<<<<<<<<<<<<<<<<")
                            ClientUtils.displayChatMessage("Only working in HuaYuTing,Thank you for using," + Memorial.CLIENT_NAME)
                            ClientUtils.displayChatMessage(">>>PosX-> $PlayerX")
                            ClientUtils.displayChatMessage(">>>PosZ-> $PlayerZ")
                            if (tpxtimes.toInt() == 0){
                                ClientUtils.displayChatMessage(">>>X teleport Time remaining -> SUCCESSFUL")
                            }else{
                                ClientUtils.displayChatMessage(">>>PX teleport Time remaining -> $remainX seconds")
                            }
                            if (tpztimes.toInt() == 0){
                                ClientUtils.displayChatMessage(">>>Z teleport Time remaining -> SUCCESSFUL")
                            }else{
                                ClientUtils.displayChatMessage(">>>Z teleport Time remaining -> $remainZ seconds")
                            }
                            if (thePlayer.posY.toInt() > height.get()){
                                ClientUtils.displayChatMessage(">>>Y teleport Time remaining -> SUCCESSFUL")
                            }else{
                                ClientUtils.displayChatMessage(">>>Y teleport Time remaining -> $remainY seconds")
                            }
                            if(thePlayer.posY.toInt() < height.get()){
                                thePlayer.setPositionAndRotation(
                                    thePlayer.posX,
                                    thePlayer.posY + 4,
                                    thePlayer.posZ,
                                    thePlayer.rotationYaw,
                                    thePlayer.rotationPitch
                                )
                            }
                            if (tpxtimes.toInt() != 0) {
                                if(tpxtimes < 0){
                                    val isNegativeX = 5
                                    ClientUtils.displayChatMessage("X-> $isNegativeX")
                                    thePlayer.setPositionAndRotation(
                                        thePlayer.posX + isNegativeX,
                                        thePlayer.posY,
                                        thePlayer.posZ,
                                        thePlayer.rotationYaw,
                                        thePlayer.rotationPitch
                                    )
                                }else{
                                    val isNegativeX = -5
                                    ClientUtils.displayChatMessage("X-> $isNegativeX")
                                    thePlayer.setPositionAndRotation(
                                        thePlayer.posX + isNegativeX,
                                        thePlayer.posY,
                                        thePlayer.posZ,
                                        thePlayer.rotationYaw,
                                        thePlayer.rotationPitch
                                    )
                                }
                            }
                            if (tpztimes.toInt() != 0){
                                if(tpztimes < 0){
                                    val isNegativeZ = 5
                                    ClientUtils.displayChatMessage("Z-> $isNegativeZ")
                                    thePlayer.setPositionAndRotation(
                                        thePlayer.posX,
                                        thePlayer.posY,
                                        thePlayer.posZ + isNegativeZ,
                                        thePlayer.rotationYaw,
                                        thePlayer.rotationPitch
                                    )
                                }else{
                                    val isNegativeZ = -5
                                    ClientUtils.displayChatMessage("Z-> $isNegativeZ")
                                    thePlayer.setPositionAndRotation(
                                        thePlayer.posX,
                                        thePlayer.posY,
                                        thePlayer.posZ + isNegativeZ,
                                        thePlayer.rotationYaw,
                                        thePlayer.rotationPitch
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                val thePlayer = mc.thePlayer ?: return
                thePlayer.capabilities.isFlying = false
                mc.timer.timerSpeed = 1f
                thePlayer.speedInAir = 0.02f
                TPtimes = Times.get()
            }
        }else{
            return
        }
    }
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        val autoSkyWars = Memorial.moduleManager.getModule(AutoSkyWars::class.java) as AutoSkyWars
        if (autoSkyWars.state) {
            val thePlayer = mc.thePlayer!!
            if (thePlayer.capabilities.isFlying) {
                when (modeValue.get().toLowerCase()) {
                    "flyaim" -> {
                        val thePlayer = mc.thePlayer ?: return

                        val range = rangeValue.get()
                        val entity = mc.theWorld!!.loadedEntityList
                            .filter {
                                EntityUtils.isSelected(it, true) && thePlayer.canEntityBeSeen(it) &&
                                        thePlayer.getDistanceToEntityBox(it) <= range && RotationUtils.getRotationDifference(
                                    it) <= 360
                            }
                            .minBy { RotationUtils.getRotationDifference(it) } ?: return

                        if (!lockValue.get() && RotationUtils.isFaced(entity, range.toDouble()))
                            return

                        val rotation = RotationUtils.limitAngleChange(
                            Rotation(thePlayer.rotationYaw, thePlayer.rotationPitch),
                            if (centerValue.get())
                                RotationUtils.toRotation(RotationUtils.getCenter(entity.entityBoundingBox), true)
                            else
                                RotationUtils.searchCenter(entity.entityBoundingBox, false, false, true,
                                    false, range).rotation,
                            (turnSpeedValue.get() + Math.random()).toFloat()
                        )
                        rotation.toPlayer(thePlayer)

                    }
                }
            }
        }
    }
    override fun handleEvents() = true
    override val tag: String
        get() = modeValue.get()
    override fun onDisable() {
        val thePlayer = mc.thePlayer!!
        thePlayer.capabilities.isFlying = false
        if (!isKeyDown(mc.gameSettings.keyBindForward))
            mc.gameSettings.keyBindForward.pressed = false

    }
    fun isKeyDown(keyBinding: KeyBinding): Boolean {
        return Keyboard.isKeyDown(keyBinding.getKeyCode())
    }
}

