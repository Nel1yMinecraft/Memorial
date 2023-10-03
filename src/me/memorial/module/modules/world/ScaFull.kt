package net.ccbluex.liquidbounce.features.module.modules.world



import dev.dudu.StaticStorage
import dev.dudu.ScaffoldUtils
import me.memorial.Memorial
import me.memorial.events.EventState
import me.memorial.events.EventTarget
import me.memorial.events.impl.misc.PacketEvent
import me.memorial.events.impl.move.JumpEvent
import me.memorial.events.impl.move.MotionEvent
import me.memorial.events.impl.move.MoveEvent
import me.memorial.events.impl.move.StrafeEvent
import me.memorial.events.impl.player.UpdateEvent
import me.memorial.events.impl.render.Render2DEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.module.modules.movement.Speed
import me.memorial.ui.font.Fonts
import me.memorial.utils.InventoryUtils
import me.memorial.utils.MovementUtils.isMoving
import me.memorial.utils.MovementUtils.strafe
import me.memorial.utils.PlaceRotation
import me.memorial.utils.Rotation
import me.memorial.utils.RotationUtils
import me.memorial.utils.block.BlockUtils.canBeClicked
import me.memorial.utils.block.BlockUtils.getBlock
import me.memorial.utils.block.BlockUtils.isReplaceable
import me.memorial.utils.block.PlaceInfo
import me.memorial.utils.block.PlaceInfo.Companion.get
import me.memorial.utils.timer.MSTimer
import me.memorial.utils.timer.TickTimer
import me.memorial.utils.timer.TimeUtils
import me.memorial.value.BoolValue
import me.memorial.value.FloatValue
import me.memorial.value.IntegerValue
import me.memorial.value.ListValue
import net.minecraft.block.BlockAir
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.settings.GameSettings
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import net.minecraft.util.MovingObjectPosition
import org.apache.commons.lang3.RandomUtils
import java.util.*

@ModuleInfo(name = "ScaFull", "我的天哪",ModuleCategory.WORLD)
class ScaFull: Module() {
    /**
     * OPTIONS (Scaffold)
     */
    // Mode
    val modeValue = ListValue("Mode", arrayOf("Normal", "Expand"), "Normal")

    //make sprint compatible with tower.add sprint tricks
    val sprintModeValue = ListValue("SprintMode", arrayOf("Same", "Ground", "Air", "PlaceOff", "None"), "None")
    val Grim = BoolValue("GrimAC", false)
    val rotationModeValue = ListValue("RotationMode", arrayOf("Normal", "AAC", "Static3"), "Normal") // searching reason
    val rotationLookupValue = ListValue("RotationLookup", arrayOf("Normal", "AAC", "Same"), "Normal")
    val speedModifierValue = FloatValue("SpeedModifier", 1f, 0f, 2f)
    val counterDisplayValue = ListValue("Counter", arrayOf("Off", "Sigma", "Novoline"), "Novoline")

    /**
     * OPTIONS (Tower)
     */
    // Global settings
    private val towerEnabled = BoolValue("EnableTower", false)
    private val towerModeValue = ListValue(
        "TowerMode",
        arrayOf("Jump", "Motion", "ConstantMotion", "MotionTP", "Packet", "Teleport", "AAC3.3.9", "AAC3.6.4", "Verus"),
        "Motion"
    )
    private val towerPlaceModeValue = ListValue("Tower-PlaceTiming", arrayOf("Pre", "Post"), "Post")
    private val stopWhenBlockAbove = BoolValue("StopWhenBlockAbove", false)
    private val onJumpValue = BoolValue("OnJump", false)
    private val noMoveOnlyValue = BoolValue("NoMove", true)
    private val towerTimerValue = FloatValue("TowerTimer", 1f, 0.1f, 10f)

    // Jump mode
    private val jumpMotionValue = FloatValue("JumpMotion", 0.42f, 0.3681289f, 0.79f)
    private val jumpDelayValue = IntegerValue("JumpDelay", 0, 0, 20)

    // ConstantMotion
    private val constantMotionValue = FloatValue("ConstantMotion", 0.42f, 0.1f, 1f)
    private val constantMotionJumpGroundValue = FloatValue("ConstantMotionJumpGround", 0.79f, 0.76f, 1f)

    // Teleport
    private val teleportHeightValue = FloatValue("TeleportHeight", 1.15f, 0.1f, 5f)
    private val maxDelayValue: IntegerValue = object : IntegerValue("MaxDelay", 0, 0, 1000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minDelayValue.get()
            if (i > newValue) set(i)
        }
    }
    private val teleportDelayValue = IntegerValue("TeleportDelay", 0, 0, 20)
    private val minDelayValue: IntegerValue = object : IntegerValue("MinDelay", 0, 0, 1000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxDelayValue.get()
            if (i < newValue) set(i)
        }
    }
    private val teleportGroundValue = BoolValue("TeleportGround", true)
    private val teleportNoMotionValue = BoolValue("TeleportNoMotion", false)

    // Delay
    private val placeableDelay = BoolValue("PlaceableDelay", false)

    // idfk what is this
    private val smartDelay = BoolValue("SmartDelay", true)

    // AutoBlock
    private val autoBlockMode = ListValue("AutoBlock", arrayOf("Spoof", "Switch", "Off"), "Spoof")
    private val stayAutoBlock = BoolValue("StayAutoBlock", false)
    private val swingValue = BoolValue("Swing", true)
    private val downValue = BoolValue("Down", false)
    private val searchValue = BoolValue("Search", true)
    private val placeModeValue = ListValue("PlaceTiming", arrayOf("Pre", "Post"), "Post")

    // Eagle
    private val eagleValue = BoolValue("Eagle", false)
    private val eagleSilentValue = BoolValue("EagleSilent", false)
    private val blocksToEagleValue = IntegerValue("BlocksToEagle", 0, 0, 10)
    private val eagleEdgeDistanceValue = FloatValue("EagleEdgeDistance", 0.2f, 0f, 0.5f)

    // Expand
    private val omniDirectionalExpand = BoolValue("OmniDirectionalExpand", false)
    private val expandLengthValue = IntegerValue("ExpandLength", 5, 1, 6)

    // Other
    // SearchAccuracy
    private val searchAccuracyValue: IntegerValue = object : IntegerValue("SearchAccuracy", 8, 1, 24) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            if (maximum < newValue) {
                set(maximum)
            } else if (minimum > newValue) {
                set(minimum)
            }
        }
    }
    private val xzRangeValue = FloatValue("xzRange", 0.8f, 0.1f, 1.0f)
    private val yRangeValue = FloatValue("yRange", 0.8f, 0.1f, 1.0f)

    // Rotations
    private val rotationsValue = BoolValue("Rotations", true)
    private val noHitCheckValue = BoolValue("NoHitCheck", false)
    private val staticPitchValue = FloatValue("Static-Pitch", 86f, 80f, 90f)
    private val keepRotOnJumpValue = BoolValue("KeepRotOnJump", true)
    private val keepRotationValue = BoolValue("KeepRotation", false)
    private val maxTurnSpeed: FloatValue = object : FloatValue("MaxTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = minTurnSpeed.get()
            if (i > newValue) set(i)
        }
    }
    private val keepLengthValue = IntegerValue("KeepRotationLength", 0, 0, 20)
    private val minTurnSpeed: FloatValue = object : FloatValue("MinTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = maxTurnSpeed.get()
            if (i < newValue) set(i)
        }
    }
    private val placeConditionValue =
        ListValue("Place-Condition", arrayOf("Air", "FallDown", "NegativeMotion", "Always", "DelayAir"), "Always")
    private val placeConditionValue2 = ListValue("Place-Condition", arrayOf("None", "DelayAir"), "None")
    private val DELAY = FloatValue("AirDelay", 0f, 0f, 1000f)
    private val E114514 = FloatValue("Test", 0f, 0f, 0.50f)
    private val rotationStrafeValue = BoolValue("RotationStrafe", false)

    // Zitter
    private val zitterValue = BoolValue("Zitter", false)
    private val zitterModeValue = ListValue("ZitterMode", arrayOf("Teleport", "Smooth"), "Teleport")
    private val zitterSpeed = FloatValue("ZitterSpeed", 0.13f, 0.1f, 0.3f)
    private val zitterStrength = FloatValue("ZitterStrength", 0.072f, 0.05f, 0.2f)
    private val zitterDelay = IntegerValue("ZitterDelay", 100, 0, 500)

    // Game
    private val timerValue = FloatValue("Timer", 1f, 0.1f, 10f)

    //    public final FloatValue xzMultiplier = new FloatValue("XZ-Multiplier", 1F, 0F, 4F);
    private val customSpeedValue = BoolValue("CustomSpeed", false)
    private val customMoveSpeedValue = FloatValue("CustomMoveSpeed", 0.3f, 0f, 5f)

    // Safety
    private val sameYValue = BoolValue("SameY", false)
    private val autoJumpValue = BoolValue("AutoJump", false)
    private val smartSpeedValue = BoolValue("SmartSpeed", false)
    private val safeWalkValue = BoolValue("SafeWalk", true)
    private val airSafeValue = BoolValue("AirSafe", false)
    private val markValue = BoolValue("Mark", true)
    private val redValue = IntegerValue("Red", 0, 0, 255)
    private val greenValue = IntegerValue("Green", 120, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val alphaValue = IntegerValue("Alpha", 120, 0, 255)
    private val autoDisableSpeedValue = BoolValue("AutoDisable-Speed", true)

    // Delay
    private val delayTimer = MSTimer()
    private val zitterTimer = MSTimer()
    private val airTimer = MSTimer()

    // Mode stuff
    private val timer = TickTimer()
    private var Ground = false
    private var air = 0f

    /**
     * MODULE
     */
    // Target block
    private var targetPlace: PlaceInfo? = null
    private var towerPlace: PlaceInfo? = null

    // Launch position
    private var launchY = 0
    private var faceBlock = false

    // Rotation lock
    private var lockRotation: Rotation? = null
    private var lookupRotation: Rotation? = null

    // Auto block slot
    private var slot = 0
    private var lastSlot = 0

    // Zitter Smooth
    private var zitterDirection = false
    private var delay: Long = 0

    // Eagle
    private var placedBlocksWithoutEagle = 0
    private var eagleSneaking = false

    // Down
    private var shouldGoDown = false

    // Render thingy
    private var progress = 0f
    private var lastMS = 0L
    private var jumpGround = 0.0
    private var verusState = 0
    private var verusJumped = false
    fun towerActivation(): Boolean {
        return towerEnabled.get() && (!onJumpValue.get() || mc.gameSettings.keyBindJump.isKeyDown) && (!noMoveOnlyValue.get() || !isMoving ())
    }

    /**
     * Enable module
     */
    override fun onEnable() {
        airTimer.reset()
        if (mc.thePlayer == null) return
        progress = 0f
        launchY = mc.thePlayer!!.posY.toInt()
        lastSlot = mc.thePlayer!!.inventory.currentItem
        slot = mc.thePlayer!!.inventory.currentItem
        if (autoDisableSpeedValue.get() && Memorial.moduleManager.getModule(Speed::class.java)!!.state) {
          Memorial.moduleManager.getModule(Speed::class.java)!!.state = false
        }
        faceBlock = false
        lastMS = System.currentTimeMillis()
    }

    //Send jump packets, bypasses Hypixel.
    private fun fakeJump() {
        mc.thePlayer!!.isAirBorne = true
    }

    /**
     * Move player
     */
    private fun move(event: MotionEvent) {
        when (towerModeValue.get().toLowerCase()) {
            "jump" -> if (mc.thePlayer!!.onGround && timer.hasTimePassed(jumpDelayValue.get())) {
                fakeJump()
                mc.thePlayer!!.motionY = jumpMotionValue.get().toDouble()
                timer.reset()
            }

            "motion" -> if (mc.thePlayer!!.onGround) {
                fakeJump()
                mc.thePlayer!!.motionY = 0.42
            } else if (mc.thePlayer!!.motionY < 0.1) mc.thePlayer!!.motionY = 0.3

            "motiontp" -> if (mc.thePlayer!!.onGround) {
                fakeJump()
                mc.thePlayer!!.motionY = 0.42
            } else if (mc.thePlayer!!.motionY < 0.23) mc.thePlayer!!.setPosition(
                mc.thePlayer!!.posX, mc.thePlayer!!.posY.toInt().toDouble(), mc.thePlayer!!.posZ
            )

            "packet" -> if (mc.thePlayer!!.onGround && timer.hasTimePassed(2)) {
                fakeJump()
                mc.netHandler.addToSendQueue(
                    C04PacketPlayerPosition(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + 0.42, mc.thePlayer!!.posZ, false
                    )
                )
                mc.netHandler.addToSendQueue(
                    C04PacketPlayerPosition(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + 0.76, mc.thePlayer!!.posZ, false
                    )
                )
                mc.thePlayer!!.setPosition(mc.thePlayer!!.posX, mc.thePlayer!!.posY + 1.08, mc.thePlayer!!.posZ)
                timer.reset()
            }

            "teleport" -> {
                if (teleportNoMotionValue.get()) mc.thePlayer!!.motionY = 0.0
                if ((mc.thePlayer!!.onGround || !teleportGroundValue.get()) && timer.hasTimePassed(teleportDelayValue.get())) {
                    fakeJump()
                    mc.thePlayer!!.setPositionAndUpdate(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + teleportHeightValue.get(),
                        mc.thePlayer!!.posZ
                    )
                    timer.reset()
                }
            }

            "constantmotion" -> {
                if (mc.thePlayer!!.onGround) {
                    fakeJump()
                    jumpGround = mc.thePlayer!!.posY
                    mc.thePlayer!!.motionY = constantMotionValue.get().toDouble()
                }
                if (mc.thePlayer!!.posY > jumpGround + constantMotionJumpGroundValue.get()) {
                    fakeJump()
                    mc.thePlayer!!.setPosition(
                        mc.thePlayer!!.posX, mc.thePlayer!!.posY.toInt()
                            .toDouble(), mc.thePlayer!!.posZ
                    )
                    mc.thePlayer!!.motionY = constantMotionValue.get().toDouble()
                    jumpGround = mc.thePlayer!!.posY
                }
            }

            "aac3.3.9" -> {
                if (mc.thePlayer!!.onGround) {
                    fakeJump()
                    mc.thePlayer!!.motionY = 0.4001
                }
                mc.timer.timerSpeed = 1f
                if (mc.thePlayer!!.motionY < 0) {
                    mc.thePlayer!!.motionY = -0.00000945
                    mc.timer.timerSpeed = 1.6f
                }
            }

            "aac3.6.4" -> if (mc.thePlayer!!.ticksExisted % 4 == 1) {
                mc.thePlayer!!.motionY = 0.4195464
                mc.thePlayer!!.setPosition(mc.thePlayer!!.posX - 0.035, mc.thePlayer!!.posY, mc.thePlayer!!.posZ)
            } else if (mc.thePlayer!!.ticksExisted % 4 == 0) {
                mc.thePlayer!!.motionY = -0.5
                mc.thePlayer!!.setPosition(mc.thePlayer!!.posX + 0.035, mc.thePlayer!!.posY, mc.thePlayer!!.posZ)
            }

            "verus" -> {
                if (!mc.theWorld!!.getCollidingBoundingBoxes(
                        mc.thePlayer!!,
                        mc.thePlayer!!.entityBoundingBox.offset(0.0, -0.01, 0.0)
                    ).isEmpty() && mc.thePlayer!!.onGround && mc.thePlayer!!.isCollidedVertically
                ) {
                    verusState = 0
                    verusJumped = true
                }
                if (verusJumped) {
                    strafe()
                    when (verusState) {
                        0 -> {
                            fakeJump()
                            mc.thePlayer!!.motionY = 0.41999998688697815
                            ++verusState
                        }

                        1 -> ++verusState
                        2 -> ++verusState
                        3 -> {
                            mc.thePlayer!!.motionY = 0.0
                            ++verusState
                        }

                        4 -> ++verusState
                    }
                    verusJumped = false
                }
                verusJumped = true
            }
        }
    }

    /**
     * Update event
     *
     * @param event
     */
    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (Grim.get()) {
            val X = Objects.requireNonNull(mc.thePlayer)!!.motionX
            val Z = mc.thePlayer!!.motionZ
            if (mc.gameSettings.keyBindForward.isKeyDown) {
                if (Z > 0.1) {
                    mc.thePlayer!!.rotationYaw = 360f
                }
                if (Z < -0.1) {
                    mc.thePlayer!!.rotationYaw = 180f
                }
                if (X > 0.1) {
                    mc.thePlayer!!.rotationYaw = 270f
                }
                if (X < -0.1) {
                    mc.thePlayer!!.rotationYaw = 90f
                }
            }
        }
        if (placeConditionValue.get().equals("delayair", ignoreCase = true)) {
            air++
            if (!Objects.requireNonNull(mc.thePlayer)!!.onGround && air > DELAY.get()) {
                keepRotationValue.set(true)
                rotationsValue.set(true)
                Ground = true
                air = 0f
            } else {
                Ground = false
            }
            if (mc.thePlayer!!.onGround) {
                air = 0f
                keepRotationValue.set(false)
                rotationsValue.set(false)
            }
        }
        if (placeConditionValue2.get().equals("delayair", ignoreCase = true)) {
            air++
            if (!Objects.requireNonNull(mc.thePlayer)!!.onGround && air > DELAY.get()) {
                keepRotationValue.set(true)
                rotationsValue.set(true)
                Ground = true
                air = 0f
            } else {
                Ground = false
            }
            if (mc.thePlayer!!.onGround) {
                air = 0f
                keepRotationValue.set(false)
                rotationsValue.set(false)
            }
        }
        if (towerActivation()) {
            shouldGoDown = false
            mc.gameSettings.keyBindSneak.pressed = false
            mc.thePlayer!!.isSprinting= false
            return
        }
        if (sprintModeValue.get().equals("PlaceOff", ignoreCase = true)) {
            if (mc.thePlayer!!.onGround);
            mc.thePlayer!!.isSprinting = true
            mc.thePlayer!!.motionX = mc.thePlayer!!.motionX
            mc.thePlayer!!.motionZ = mc.thePlayer!!.motionZ
        }
        mc.timer.timerSpeed = timerValue.get()
        shouldGoDown =
            downValue.get() && !sameYValue.get() &&!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && blocksAmount > 1
        if (shouldGoDown) mc.gameSettings.keyBindSneak.pressed = false

        // scaffold custom speed if enabled
        if (customSpeedValue.get()) strafe(customMoveSpeedValue.get())
        if (mc.thePlayer!!.onGround) {
            val mode = modeValue.get()

            // Rewinside scaffold mode
            if (mode.equals("Rewinside", ignoreCase = true)) {
                strafe(0.2f)
                mc.thePlayer!!.motionY = 0.0
            }

            // Smooth Zitter
            if (zitterValue.get() && zitterModeValue.get().equals("smooth", ignoreCase = true)) {
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindRight)) mc.gameSettings.keyBindRight.pressed =
                    false
                if (GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)) mc.gameSettings.keyBindLeft.pressed = false
                if (zitterTimer.hasTimePassed(zitterDelay.get().toLong())) {
                    zitterDirection = !zitterDirection
                    zitterTimer.reset()
                }
                if (zitterDirection) {
                    mc.gameSettings.keyBindRight.pressed = true
                    mc.gameSettings.keyBindLeft.pressed = false
                } else {
                    mc.gameSettings.keyBindRight.pressed = false
                    mc.gameSettings.keyBindLeft.pressed = true
                }
            }

            // Eagle
            if (eagleValue.get() && !shouldGoDown) {
                var dif = 0.5
                if (eagleEdgeDistanceValue.get() > 0) {
                    for (i in 0..3) {
                        val WBlockPos = BlockPos(
                            mc.thePlayer!!.posX + if (i == 0) -1 else if (i == 1) 1 else 0,
                            mc.thePlayer!!.posY - if (mc.thePlayer!!.posY == mc.thePlayer!!.posY.toInt() + 0.5) 0.0 else 1.0,
                            mc.thePlayer!!.posZ + if (i == 2) -1 else if (i == 3) 1 else 0
                        )
                        val placeInfo = get(WBlockPos)
                        if (isReplaceable(WBlockPos) && placeInfo != null) {
                            var calcDif =
                                if (i > 1) mc.thePlayer!!.posZ - WBlockPos.z else mc.thePlayer!!.posX - WBlockPos.x
                            calcDif -= 0.5
                            if (calcDif < 0) calcDif *= -1.0
                            calcDif -= 0.5
                            if (calcDif < dif) dif = calcDif
                        }
                    }
                }
                if (placedBlocksWithoutEagle >= blocksToEagleValue.get()) {
                    val shouldEagle = mc.theWorld!!.getBlockState(
                        BlockPos(mc.thePlayer!!.posX, mc.thePlayer!!.posY - 1.0, mc.thePlayer!!.posZ)
                    ).block == Blocks.air || dif < eagleEdgeDistanceValue.get()
                    if (eagleSilentValue.get()) {
                        if (eagleSneaking != shouldEagle) {
                            mc.netHandler.addToSendQueue(
                                C0BPacketEntityAction(
                                    mc.thePlayer!!,
                                    if (shouldEagle) C0BPacketEntityAction.Action.START_SNEAKING else C0BPacketEntityAction.Action.STOP_SNEAKING
                                )
                            )
                        }
                        eagleSneaking = shouldEagle
                    } else mc.gameSettings.keyBindSneak.pressed = shouldEagle
                    placedBlocksWithoutEagle = 0
                } else placedBlocksWithoutEagle++
            }

            // Zitter
            if (zitterValue.get() && zitterModeValue.get().equals("teleport", ignoreCase = true)) {
                strafe(zitterSpeed.get())
                val yaw = Math.toRadians(mc.thePlayer!!.rotationYaw + if (zitterDirection) 90.0 else -90.0)
                mc.thePlayer!!.motionX = -Math.sin(yaw) * zitterStrength.get()
                mc.thePlayer!!.motionZ = Math.cos(yaw) * zitterStrength.get()
                zitterDirection = !zitterDirection
            }
        }
        if (sprintModeValue.get().equals("off", ignoreCase = true) || sprintModeValue.get()
                .equals("air", ignoreCase = true) && mc.thePlayer!!.onGround
        ) {
            mc.thePlayer!!.isSprinting= true
        }
        if (sprintModeValue.get().equals("ground", ignoreCase = true)) {
            mc.thePlayer!!.isSprinting = mc.thePlayer!!.onGround
        }
        //Auto Jump thingy
        if (shouldGoDown) {
            launchY = mc.thePlayer!!.posY.toInt() - 1
        } else if (!sameYValue.get()) {
            if (!autoJumpValue.get() && !(smartSpeedValue.get() &&Memorial.moduleManager.getModule(
                    Speed::class.java
                )!!.state) || GameSettings.isKeyDown(mc.gameSettings.keyBindJump) || mc.thePlayer!!.posY < launchY
            ) launchY = mc.thePlayer!!.posY.toInt()
            if (autoJumpValue.get() && !Memorial.moduleManager.getModule(Speed::class.java)!!.state && isMoving() && mc.thePlayer!!.onGround) {
                mc.thePlayer!!.jump()
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null) return
        val packet = event.packet

        // AutoBlock
        if (packet is C09PacketHeldItemChange) {
            val packetHeldItemChange = packet as C09PacketHeldItemChange
            slot = packetHeldItemChange.slotId
        }
    }

    @EventTarget //took it from applyrotationstrafe XD. staticyaw comes from bestnub.
    fun onStrafe(event: StrafeEvent) {
        if (lookupRotation != null && rotationStrafeValue.get()) {
            val dif =
                ((MathHelper.wrapDegrees(mc.thePlayer!!.rotationYaw - lookupRotation!!.yaw - 23.5f - 135) + 180) / 45).toInt()
            val yaw = lookupRotation!!.yaw
            val strafe = event.strafe
            val forward = event.forward
            val friction = event.friction
            var calcForward = 0f
            var calcStrafe = 0f
            when (dif) {
                0 -> {
                    calcForward = forward
                    calcStrafe = strafe
                }

                1 -> {
                    calcForward += forward
                    calcStrafe -= forward
                    calcForward += strafe
                    calcStrafe += strafe
                }

                2 -> {
                    calcForward = strafe
                    calcStrafe = -forward
                }

                3 -> {
                    calcForward -= forward
                    calcStrafe -= forward
                    calcForward += strafe
                    calcStrafe -= strafe
                }

                4 -> {
                    calcForward = -forward
                    calcStrafe = -strafe
                }

                5 -> {
                    calcForward -= forward
                    calcStrafe += forward
                    calcForward -= strafe
                    calcStrafe -= strafe
                }

                6 -> {
                    calcForward = -strafe
                    calcStrafe = forward
                }

                7 -> {
                    calcForward += forward
                    calcStrafe += forward
                    calcForward -= strafe
                    calcStrafe += strafe
                }
            }
            if (calcForward > 1f || calcForward < 0.9f && calcForward > 0.3f || calcForward < -1f || calcForward > -0.9f && calcForward < -0.3f) {
                calcForward *= 0.5f
            }
            if (calcStrafe > 1f || calcStrafe < 0.9f && calcStrafe > 0.3f || calcStrafe < -1f || calcStrafe > -0.9f && calcStrafe < -0.3f) {
                calcStrafe *= 0.5f
            }
            var f = calcStrafe * calcStrafe + calcForward * calcForward
            if (f >= 1.0E-4f) {
                f = MathHelper.sqrt(f)
                if (f < 1.0f) f = 1.0f
                f = friction / f
                calcStrafe *= f
                calcForward *= f
                val yawSin = MathHelper.sin((yaw * Math.PI / 180f).toFloat())
                val yawCos = MathHelper.cos((yaw * Math.PI / 180f).toFloat())
                mc.thePlayer!!.motionX = (calcStrafe * yawCos - calcForward * yawSin).toDouble()
                mc.thePlayer!!.motionZ = (calcForward * yawCos + calcStrafe * yawSin).toDouble()
            }
            event.cancelEvent()
        }
    }

    private fun shouldPlace(): Boolean {
        val placeDelayAir = placeConditionValue.get().equals("delayair", ignoreCase = true)
        val placeDelayAir2 = placeConditionValue2.get().equals("delayair", ignoreCase = true)
        val placeWhenAir = placeConditionValue.get().equals("air", ignoreCase = true)
        val placeWhenFall = placeConditionValue.get().equals("falldown", ignoreCase = true)
        val placeWhenNegativeMotion = placeConditionValue.get().equals("negativemotion", ignoreCase = true)
        val alwaysPlace = placeConditionValue.get().equals("always", ignoreCase = true)
        val e114514 = placeConditionValue.get().equals("114514", ignoreCase = true)
        return (towerActivation()
                || alwaysPlace || e114514 && Objects.requireNonNull(mc.theWorld)!!.getCollidingBoundingBoxes(
            Objects.requireNonNull(mc.thePlayer)!!, mc.thePlayer!!.entityBoundingBox
                .offset(0.0, -0.5, 0.0).expand(-E114514.get().toDouble(), 0.0, -E114514.get().toDouble())
        )
            .isEmpty()) || placeWhenAir && !mc.thePlayer!!.onGround || placeDelayAir && Ground || placeDelayAir2 && Ground || placeWhenFall && Objects.requireNonNull(
            mc.thePlayer
        )!!.fallDistance > 0 || placeWhenNegativeMotion && Objects.requireNonNull(mc.thePlayer)!!.motionY < 0
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {

        // Lock Rotation
        if (rotationsValue.get() && keepRotationValue.get() && lockRotation != null) RotationUtils.setTargetRotation(
            RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                lockRotation,
                RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())
            )
        )
        val mode = modeValue.get()
        val eventState = event.state
        if ((!rotationsValue.get() || noHitCheckValue.get() || faceBlock) && placeModeValue.get()
                .equals(eventState.stateName, ignoreCase = true) && !towerActivation()
        ) {
            place(false)
        }
        if (eventState === EventState.PRE && !towerActivation()) {
            if (!shouldPlace() || (if (!autoBlockMode.get()
                        .equals("Off", ignoreCase = true)
                ) InventoryUtils.findAutoBlockBlock() == -1 else mc.thePlayer!!.heldItem == null ||
                        mc.thePlayer!!.heldItem!!.item !is ItemBlock)
            ) return
            findBlock(mode.equals("expand", ignoreCase = true) && !towerActivation())
        }
        if (targetPlace == null) {
            if (placeableDelay.get()) delayTimer.reset()
        }
        if (!towerActivation()) {
            verusState = 0
            towerPlace = null
            return
        }
        mc.timer.timerSpeed = towerTimerValue.get()
        if (towerPlaceModeValue.get().equals(eventState.stateName, ignoreCase = true)) place(true)
        if (eventState === EventState.PRE) {
            towerPlace = null
            timer.update()
            val isHeldItemBlock = mc.thePlayer!!.heldItem != null && mc.thePlayer!!.heldItem!!.item is ItemBlock
            if (InventoryUtils.findAutoBlockBlock() != -1 || isHeldItemBlock) {
                launchY = mc.thePlayer!!.posY.toInt()
                if (towerModeValue.get().equals("verus", ignoreCase = true) || !stopWhenBlockAbove.get() || getBlock(
                        BlockPos(
                            mc.thePlayer!!.posX,
                            mc.thePlayer!!.posY + 2, mc.thePlayer!!.posZ
                        )
                    ) is BlockAir
                ) move(event)
                val WBlockPos = BlockPos(mc.thePlayer!!.posX, mc.thePlayer!!.posY - 1.0, mc.thePlayer!!.posZ)
                if (mc.theWorld!!.getBlockState(WBlockPos).block is BlockAir) {
                    if (search(WBlockPos, true, true) && rotationsValue.get()) {
                        val vecRotation = RotationUtils.faceBlock(WBlockPos)
                        if (vecRotation != null) {
                            RotationUtils.setTargetRotation(
                                RotationUtils.limitAngleChange(
                                    RotationUtils.serverRotation,
                                    vecRotation.rotation,
                                    RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())
                                )
                            )
                            towerPlace!!.vec3 = vecRotation.vec
                        }
                    }
                }
            }
        }
    }

    /**
     * Search for new target block
     */
    private fun findBlock(expand: Boolean) {
        val WBlockPosition = if (shouldGoDown) (if (mc.thePlayer!!.posY == mc.thePlayer!!.posY.toInt() + 0.5) BlockPos(
            mc.thePlayer!!.posX, mc.thePlayer!!.posY - 0.6, mc.thePlayer!!.posZ
        ) else BlockPos(
            mc.thePlayer!!.posX, mc.thePlayer!!.posY - 0.6, mc.thePlayer!!.posZ
        ).down()) else if (!towerActivation() && (sameYValue.get() || (autoJumpValue.get() || smartSpeedValue.get() && Memorial.moduleManager.getModule(
                Speed::class.java
            )!!.state) && GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) && launchY <= mc.thePlayer!!.posY
        ) BlockPos(
            mc.thePlayer!!.posX, (launchY - 1).toDouble(), mc.thePlayer!!.posZ
        ) else if (mc.thePlayer!!.posY == mc.thePlayer!!.posY.toInt() + 0.5) BlockPos(
            mc.thePlayer!!
        ) else BlockPos(
            mc.thePlayer!!.posX, mc.thePlayer!!.posY, mc.thePlayer!!.posZ
        ).down()
        if (!expand && (!isReplaceable(WBlockPosition) || search(WBlockPosition, !shouldGoDown, false))) return
        if (expand) {
            val yaw = Math.toRadians((mc.thePlayer!!.rotationYaw + 180).toDouble())
            val x = if (omniDirectionalExpand.get()) Math.round(-Math.sin(yaw))
                .toInt() else mc.thePlayer!!.horizontalFacing.directionVec.x
            val z = if (omniDirectionalExpand.get()) Math.round(Math.cos(yaw))
                .toInt() else mc.thePlayer!!.horizontalFacing.directionVec.z
            for (i in 0 until expandLengthValue.get()) {
                if (search(WBlockPosition.add(x * i, 0, z * i), false, false)) return
            }
        } else if (searchValue.get()) {
            for (x in -1..1) for (z in -1..1) if (search(WBlockPosition.add(x, 0, z), !shouldGoDown, false)) return
        }
    }

    /**
     * Place target block
     */
    private fun place(towerActive: Boolean) {
        if (sprintModeValue.get().equals("PlaceOff", ignoreCase = true)) {
            mc.thePlayer!!.isSprinting = false
            mc.thePlayer!!.motionX = mc.thePlayer!!.motionX
            mc.thePlayer!!.motionZ = mc.thePlayer!!.motionZ
        }
        if ((if (towerActive) towerPlace else targetPlace) == null) {
            if (placeableDelay.get()) delayTimer.reset()
            return
        }
        if (!towerActivation() && (!delayTimer.hasTimePassed(delay) || smartDelay.get() && mc.rightClickDelayTimer > 0 || (sameYValue.get() || (autoJumpValue.get() || smartSpeedValue.get() && Memorial.moduleManager.getModule(
                Speed::class.java
            )!!.state) && GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) && launchY - 1 != (if (towerActive) towerPlace else targetPlace)!!.vec3.yCoord.toInt())
        ) return
        var blockSlot = -1
        var itemStack = mc.thePlayer!!.heldItem
        if (mc.thePlayer!!.heldItem == null || mc.thePlayer!!.heldItem!!.item !is ItemBlock) {
            if (autoBlockMode.get().equals("Off", ignoreCase = true)) return
            blockSlot = InventoryUtils.findAutoBlockBlock()
            if (blockSlot == -1) return
            if (autoBlockMode.get().equals("Matrix", ignoreCase = true) && blockSlot - 36 != slot) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(blockSlot - 36))
            }
            if (autoBlockMode.get().equals("Spoof", ignoreCase = true)) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(blockSlot - 36))
                itemStack = mc.thePlayer!!.inventoryContainer.getSlot(blockSlot).stack
            } else {
                mc.thePlayer!!.inventory.currentItem = blockSlot - 36
                mc.playerController.updateController()
            }
        }

        // blacklist check
        if (itemStack != null && itemStack.item != null && itemStack.item is ItemBlock) {
            val itemBlock = itemStack.item!! as ItemBlock
            val block = itemBlock.block

        }
        if (mc.playerController.onPlayerRightClick(
                mc.thePlayer!!,
                mc.theWorld!!,
                itemStack,
                (if (towerActive) towerPlace else targetPlace)!!.blockPos,
                (if (towerActive) towerPlace else targetPlace)!!.enumFacing,
                (if (towerActive) towerPlace else targetPlace)!!.vec3
            )
        ) {
            delayTimer.reset()
            delay = if (!placeableDelay.get()) 0L else TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
            if (mc.thePlayer!!.onGround) {
                val modifier = speedModifierValue.get()
                mc.thePlayer!!.motionX = mc.thePlayer!!.motionX * modifier
                mc.thePlayer!!.motionZ = mc.thePlayer!!.motionZ * modifier
            }
            if (swingValue.get()) mc.thePlayer!!.swingItem()
        }

        // Reset
        if (towerActive) towerPlace = null else targetPlace = null
        if (!stayAutoBlock.get() && blockSlot >= 0 && !autoBlockMode.get()
                .equals("Switch", ignoreCase = true)
        ) mc.netHandler.addToSendQueue(
            C09PacketHeldItemChange(
                mc.thePlayer!!.inventory.currentItem
            )
        )
    }

    /**
     * Disable scaffold module
     */
    override fun onDisable() {
        airTimer.reset()
        if (mc.thePlayer == null) return
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            mc.gameSettings.keyBindSneak.pressed = false
            if (eagleSneaking) mc.netHandler.addToSendQueue(
                C0BPacketEntityAction(
                    mc.thePlayer!!,C0BPacketEntityAction.Action.STOP_SNEAKING
                )
            )
        }
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindRight)) mc.gameSettings.keyBindRight.pressed = false
        if (GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)) mc.gameSettings.keyBindLeft.pressed = false
        lockRotation = null
        lookupRotation = null
        mc.timer.timerSpeed = 1f
        shouldGoDown = false
        faceBlock = false
        if (lastSlot != mc.thePlayer!!.inventory.currentItem && autoBlockMode.get()
                .equals("switch", ignoreCase = true)
        ) {
            mc.thePlayer!!.inventory.currentItem = lastSlot
            mc.playerController.updateController()
        }
        if (slot != mc.thePlayer!!.inventory.currentItem && autoBlockMode.get()
                .equals("spoof", ignoreCase = true)
        ) mc.netHandler.addToSendQueue(
            C09PacketHeldItemChange(
                mc.thePlayer!!.inventory.currentItem
            )
        )
    }

    /**
     * Entity movement event
     *
     * @param event
     */
    @EventTarget
    fun onMove(event: MoveEvent) {
        if (!safeWalkValue.get() || shouldGoDown) return
        if (airSafeValue.get() || mc.thePlayer!!.onGround) event.isSafeWalk = true
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (towerActivation()) event.cancelEvent()
    }

    /**
     * Scaffold visuals
     *
     * @param event
     */
    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        progress = (System.currentTimeMillis() - lastMS).toFloat() / 100f
        if (progress >= 1) progress = 1f
        val counterMode = counterDisplayValue.get()
        val scaledResolution = ScaledResolution(mc)
        val info = blocksAmount.toString() + " blocks"
        val infoWidth = Fonts.font35.getStringWidth(info)
        val infoWidth2 = Fonts.minecraftFont.getStringWidth(blocksAmount.toString())
        if (counterMode.equals("simple", ignoreCase = true)) {
            Fonts.minecraftFont.drawString(
                blocksAmount.toString(),
                (scaledResolution.scaledWidth / 2 - infoWidth2 / 2 - 1).toFloat(),
                (scaledResolution.scaledHeight / 2 - 36).toFloat(),
                -0x1000000,
                false
            )
            Fonts.minecraftFont.drawString(
                blocksAmount.toString(),
                (scaledResolution.scaledWidth / 2 - infoWidth2 / 2 + 1).toFloat(),
                (scaledResolution.scaledHeight / 2 - 36).toFloat(),
                -0x1000000,
                false
            )
            Fonts.minecraftFont.drawString(
                blocksAmount.toString(),
                (scaledResolution.scaledWidth / 2 - infoWidth2 / 2).toFloat(),
                (scaledResolution.scaledHeight / 2 - 35).toFloat(),
                -0x1000000,
                false
            )
            Fonts.minecraftFont.drawString(
                blocksAmount.toString(),
                (scaledResolution.scaledWidth / 2 - infoWidth2 / 2).toFloat(),
                (scaledResolution.scaledHeight / 2 - 37).toFloat(),
                -0x1000000,
                false
            )
            Fonts.minecraftFont.drawString(
                blocksAmount.toString(),
                (scaledResolution.scaledWidth / 2 - infoWidth2 / 2).toFloat(),
                (scaledResolution.scaledHeight / 2 - 36).toFloat(),
                -1,
                false
            )
        }

        if (counterMode.equals("novoline", ignoreCase = true)) {
            if (slot >= 0 && slot < 9 && mc.thePlayer!!.inventory.mainInventory[slot] != null && mc.thePlayer!!.inventory.mainInventory[slot]!!.item != null && mc.thePlayer!!.inventory.mainInventory[slot]!!.item is ItemBlock) {
                //RenderUtils.drawRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 26, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - 5, 0xA0000000);
                GlStateManager.pushMatrix()
                GlStateManager.translate(
                    (scaledResolution.scaledWidth / 2 - 22).toFloat(),
                    (scaledResolution.scaledHeight / 2 + 16).toFloat(),
                    (scaledResolution.scaledWidth / 2 - 22).toFloat()
                )
                renderItemStack(mc.thePlayer!!.inventory.mainInventory[slot]!!, 0, 0)
                GlStateManager.popMatrix()
            }
            GlStateManager.resetColor()
            Fonts.minecraftFont.drawString(
                blocksAmount.toString() + " blocks",
                (scaledResolution.scaledWidth / 2).toFloat(),
                (scaledResolution.scaledHeight / 2 + 20).toFloat(),
                -1,
                true
            )
        }
    }


    private fun renderItemStack(stack: ItemStack, x: Int, y: Int) {
        GlStateManager.pushMatrix()
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderHelper.enableGUIStandardItemLighting()
        mc.renderItem.renderItemAndEffectIntoGUI(stack, x, y)
        mc.renderItem.renderItemOverlays(mc.fontRendererObj, stack, x, y)
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    private fun calcStepSize(range: Double): Double {
        var accuracy = searchAccuracyValue.get().toDouble()
        accuracy += accuracy % 2 // If it is set to uneven it changes it to even. Fixes a bug
        return if (range / accuracy < 0.01) 0.01 else range / accuracy
    }

    private fun search(WBlockPosition: BlockPos, checks: Boolean): Boolean {
        return search(WBlockPosition, checks, false)
    }

    /**
     * Search for placeable block
     *
     * @param blockPosition pos
     * @param checks        visible
     * @return
     */
    fun search(blockPosition: BlockPos, checks: Boolean, towerActive: Boolean): Boolean {
        faceBlock = false
        // SearchRanges
        val xzRV = xzRangeValue.get().toDouble()
        val xzSSV = calcStepSize(xzRV)
        val yRV = yRangeValue.get().toDouble()
        val ySSV = calcStepSize(yRV)
        val xSearchFace = 0.0
        val ySearchFace = 0.0
        val zSearchFace = 0.0
        if (!isReplaceable(blockPosition)) return false
        val staticYawMode = rotationLookupValue.get().equals("AAC", ignoreCase = true) || rotationLookupValue.get()
            .equals("same", ignoreCase = true) && (rotationModeValue.get()
            .equals("AAC", ignoreCase = true) || rotationModeValue.get().contains("Static") && !rotationModeValue.get()
            .equals("static3", ignoreCase = true))
        val eyesPos = net.minecraft.util.Vec3(
            mc.thePlayer.posX,
            mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight(),
            mc.thePlayer.posZ
        )
        var placeRotation: PlaceRotation? = null
        for (side in StaticStorage.facings()) {
            val neighbor = blockPosition.offset(side)
            if (!canBeClicked(neighbor)) continue
            val dirVec = net.minecraft.util.Vec3(side.directionVec)
            var xSearch = 0.5 - xzRV / 2
            while (xSearch <= 0.5 + xzRV / 2) {
                var ySearch = 0.5 - yRV / 2
                while (ySearch <= 0.5 + yRV / 2) {
                    var zSearch = 0.5 - xzRV / 2
                    while (zSearch <= 0.5 + xzRV / 2) {
                        val posVec = net.minecraft.util.Vec3(blockPosition).addVector(xSearch, ySearch, zSearch)
                        val distanceSqPosVec = eyesPos.squareDistanceTo(posVec)
                        val hitVec = posVec.add(
                            net.minecraft.util.Vec3(
                                dirVec.xCoord * 0.5,
                                dirVec.yCoord * 0.5,
                                dirVec.zCoord * 0.5
                            )
                        )
                        if (checks && (eyesPos.squareDistanceTo(hitVec) > 18.0 || distanceSqPosVec > eyesPos.squareDistanceTo(
                                posVec.add(dirVec)
                            ) || mc.theWorld.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null)
                        ) {
                            zSearch += xzSSV
                            continue
                        }

                        // face block
                        for (i in 0 until if (staticYawMode) 2 else 1) {
                            val diffX: Double = if (staticYawMode && i == 0) 0.0
                            else hitVec.xCoord - eyesPos.xCoord
                            val diffY = hitVec.yCoord - eyesPos.yCoord
                            val diffZ: Double = if (staticYawMode && i == 1) 0.0
                            else hitVec.zCoord - eyesPos.zCoord
                            val diffXZ =
                                net.minecraft.util.MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ).toDouble()
                            var rotation = Rotation(
                                net.minecraft.util.MathHelper.wrapAngleTo180_float(
                                    Math.toDegrees(Math.atan2(diffZ, diffX)).toFloat() - 90f
                                ),
                                net.minecraft.util.MathHelper.wrapAngleTo180_float(-Math.toDegrees(Math.atan2(diffY, diffXZ)).toFloat())
                            )
                            lookupRotation = rotation
                            if (rotationModeValue.get().equals(
                                    "static",
                                    ignoreCase = true
                                ) && (keepRotOnJumpValue.get() || !mc.gameSettings.keyBindJump.isKeyDown)
                            ) rotation = Rotation(
                                ScaffoldUtils.getScaffoldRotation(
                                    mc.thePlayer.rotationYaw, mc.thePlayer.moveForward
                                ), staticPitchValue.get()
                            )
                            if ((rotationModeValue.get().equals("static2", ignoreCase = true) || rotationModeValue.get()
                                    .equals(
                                        "static3",
                                        ignoreCase = true
                                    )) && (keepRotOnJumpValue.get() || !mc.gameSettings.keyBindJump.isKeyDown)
                            ) rotation = Rotation(rotation.yaw, staticPitchValue.get())
                            val rotationVector = RotationUtils.getVectorForRotation(
                                if (rotationLookupValue.get()
                                        .equals("same", ignoreCase = true)
                                ) rotation else lookupRotation
                            )
                            val vector = eyesPos.addVector(
                                rotationVector.xCoord * 4,
                                rotationVector.yCoord * 4,
                                rotationVector.zCoord * 4
                            )
                            val obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true)
                            if (!(obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && obj.blockPos == neighbor)) continue
                            if (placeRotation == null || RotationUtils.getRotationDifference(rotation) < RotationUtils.getRotationDifference(
                                    placeRotation.rotation
                                )
                            ) placeRotation = PlaceRotation(PlaceInfo(neighbor, side.opposite, hitVec), rotation)
                        }
                        zSearch += xzSSV
                    }
                    ySearch += ySSV
                }
                xSearch += xzSSV
            }
        }
        if (placeRotation == null) return false
        if (rotationsValue.get()) {
            if (minTurnSpeed.get() < 180) {
                val limitedRotation = RotationUtils.limitAngleChange(
                    RotationUtils.serverRotation,
                    placeRotation.rotation,
                    RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())
                )
                if ((10 * net.minecraft.util.MathHelper.wrapAngleTo180_float(limitedRotation.yaw)) as Int == (10 * net.minecraft.util.MathHelper.wrapAngleTo180_float(
                        placeRotation.rotation.yaw
                    )) as Int
                    && (10 * net.minecraft.util.MathHelper.wrapAngleTo180_float(limitedRotation.pitch)) as Int == (10 * net.minecraft.util.MathHelper.wrapAngleTo180_float(
                        placeRotation.rotation.pitch
                    )) as Int
                ) {
                    RotationUtils.setTargetRotation(placeRotation.rotation, keepLengthValue.get())
                    lockRotation = placeRotation.rotation
                    faceBlock = true
                } else {
                    RotationUtils.setTargetRotation(limitedRotation, keepLengthValue.get())
                    lockRotation = limitedRotation
                    faceBlock = false
                }
            } else {
                RotationUtils.setTargetRotation(placeRotation.rotation, keepLengthValue.get())
                lockRotation = placeRotation.rotation
                faceBlock = true
            }
            if (rotationLookupValue.get().equals("same", ignoreCase = true)) lookupRotation = lockRotation
        }
        if (towerActive) towerPlace = placeRotation.placeInfo else targetPlace = placeRotation.placeInfo
        return true
    }

    private val blocksAmount: Int
        /**
         * @return hotbar blocks amount
         */
        private get() {
            var amount = 0
            for (i in 36..44) {
                val itemStack = mc.thePlayer!!.inventoryContainer.getSlot(i).stack
                if (itemStack != null && itemStack.item is ItemBlock) {
                    val block = itemStack.item as ItemBlock
                    val heldItem = mc.thePlayer!!.heldItem
                    if (heldItem != null)
                        block

                    amount += itemStack.stackSize
                }
            }
            return amount
        }
    override val tag: String
        get() = modeValue.get()
}