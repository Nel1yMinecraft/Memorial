package dev.nelly.sk1d.module

//Thx LiquidBounce
//只在花雨庭空岛战争工作

//你说得对，代码有不合理的地方都是我故意留的，比如你其实可以fun一个来调用的tp的
//还有，我是开源的，你觉得你能绕得更好我不说话，我的代码只是给你参考用的，你爱怎么玩你自己改

//flyaim模式 的话 flyspeed 建议是1.3~1.5
//teleport模式 的高度你随便拉
//posY拉-4就是Asaka 的noclip高仿版本，模式选fly或者firework

//老弟，你是不是米饭团子吃多的，我说你连按键显示有外置的你都不知道，而且还有易语言只能写注入的这种逆天发言

/*
你当我是弱智就好
*你可以拿去圈钱但是请尊重我，我没有觉得任何人没有权利skid我的代码也不要辱骂skid我的代码的人
*他们只是没有想象力，他们也在学习，任何人都可以成为一个好的自写者，只要他们肯学
*挂圈不只是金钱和恶俗开户，挂圈需要绕过，而不是永无止境的skid源码或者思路
*如果你能总结这个思路那么你就是在进步，对了给你个提示32有办法绕一个离谱的东西，能让你开局3床没有问题，我已经绕过，但是无法公开，并且我的开源群也会锁群
*我开学了，没空理你这个儍閉的问题，如果你打滑都不明白建议去学基础
*我不喜欢魔怔和恶俗，你没有实力，我做到的是我的东西能开源就开源，不能开源的我就无法获取，不是拿金钱能买到的。我讨厌给水影加验证的人，免费开源的东西为什么能拿来圈钱，哪个绕过是出自你自己写的？
*我是一名公益者，理应受到尊重，你没有拿得出的绕过，你有但是你做不到像我一样开源，除了查9族来威胁我别开源这样真的对吗，况且为什么你查出来的人在缅甸
*我的户籍在20年被开过，然后就销声匿迹了，此后包括所有的账号的Phone、护脊、以及任何钱包都不是我的，你知道的，我是一名公益者，没有发过任何的收款码，如果有不要相信。我在的内部做的代理，别人请我去但是我是一分钱不要的，转手给别人了。我不需要钱，我现在只是学生，学。钱，这个钱赚得不好，为什么？开源的东西为什么我要拿来圈钱，就像"破解了liquidbounce然后售卖一样蠢"
*这个手机号说起来也有历史了，具体不说了，反正我当时遇到了这个号主，然后这人急眼开护脊了，然后我就给他QQ发给了一个骗子，然后最近知道了在缅北
*然后我花了2000买了他的手机号和护脊。这是我唯一一次被开，也是最后一次。这是我干过最恶劣的事情，因为他骚扰我的家人，让我家人不好，那我也让他家人不好受。但是他被骗去时我不知道那个是人口贩子，所以我罪该万死。这可能是我这年最后一次登录这个账号。我坦白了。你想报警吗？对不起，我试过了，警察也找不回来，那时候鉴于是小孩子不懂事，所以没有罚。。。
*/


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
        "teleport"//用不了你可以不用
    ), "flyaim")
    private val X = IntegerValue("posX", 0, -100, 100)
    private val Y = IntegerValue("posY", 10, 0, 50)
    private val Z = IntegerValue("posZ", 0, -100, 100)
    private var SilentNoChat = BoolValue("SilentNoChat",false)
    //private val TeleportX = IntegerValue("Teleport posX", 0, -100, 100)
    //private val TeleportY = IntegerValue("Teleport posY", 4, 0, 5)
    //private val TeleportZ = IntegerValue("Teleport posZ", 0, -100, 100)
    private val vanillaSpeedValue = FloatValue("FlySpeed", 4f, 0f, 10f)
    private val Times = IntegerValue("TpTicks", 10, 0, 100)
    private var TPtimes = Times.get()
    private val rangeValue = FloatValue("AimRange", 114514F, 1F, 114514F)
    private val centerValue = BoolValue("Center", false)
    private val lockValue = BoolValue("Lock", true)
    private val turnSpeedValue = FloatValue("TurnSpeed", 360F, 360F, 114514F)
    private val height = IntegerValue("Teleportheight", 190, 0, 256)



    //废弃代码
    //@EventTarget
    //fun onPacket(event: PacketEvent) {
    //    val packet = event.packet
    //    val thePlayer = mc.thePlayer!!
    //   val TpX =  TeleportX.get().toDouble()
    //   val TpY =  TeleportY.get().toDouble()
    //   val TpZ =  TeleportZ.get().toDouble()
    //   val packetPlayer = packet.asCPacketPlayer()
    //   if (thePlayer.capabilities.isFlying) {
    //     when (modeValue.get().toLowerCase()) {Caiji
    //     "Teleport" -> {                                                                     /////////
    //           when(TPtimes !== 0){                                                       //                                  //          //      //
    //              true -> {                                                              //                                    //          //      //
    //                        ClientUtils.displayChatMessage("[修改空岛出生点]芜湖!起飞！！")//                      //////
    //                        thePlayer.setPositionAndRotation(                         //                     //     ////      ///         ///     ///
    //                            thePlayer.posX + X.get(),                               //                 //         ///     ///         ///     ///
    //                            thePlayer.posY + Y.get(),                               //                 //          //     ///         ///     ///
    //                            thePlayer.posZ + Z.get(),                                 //          ///   //       ////     ///         ///     ///
    //                            thePlayer.rotationYaw,                                      /////////         ////////  //    ///         ///     ///
    ////////////                           thePlayer.rotationPitch                                                                          ///
    //      )                                                                                                                         //    ///
    //      TPtimes -= 1                                                                                                                 ///
    //   }
    // else -> {
    //   ClientUtils.displayChatMessage("[修改空岛出生点]TP......")
    // packetPlayer.x = TpX
    //   packetPlayer.y = TpY
    //                 packetPlayer.z = TpZ
    //                }
    //          }
    //    }
    //}
    // }
    //}

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
                                //Vanilla Fly ↓
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
                        }else{//debug ↓
                            val PlayerX = thePlayer.posX
                            val PlayerZ = thePlayer.posZ
                            val tpxtimes = PlayerX / 5//因为下面是每次TP 5格，所以这里就是自己的坐标除以5，比如是：玩家此时的X轴坐标为-100，那tpxtimes就是-20次，然后下面加-20*-5就是-100+100 = 0
                            val tpztimes = PlayerZ / 5
                            val remainZ = tpztimes / 20//计算时间，因为Update是每个ticks都，所以除20得到秒
                            val remainX = tpxtimes / 20
                            val remainY = thePlayer.posY / 20
                            ClientUtils.displayChatMessage(">>>>>>>>>>>>>>>>>>SkyWars Helper<<<<<<<<<<<<<<<<<<")
                            ClientUtils.displayChatMessage("Code by 菜级玩家(bilibili.com)")
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
                            if(thePlayer.posY.toInt() < height.get()){//出玻璃用的
                                thePlayer.setPositionAndRotation(
                                    thePlayer.posX,
                                    thePlayer.posY + 4,
                                    thePlayer.posZ,
                                    thePlayer.rotationYaw,
                                    thePlayer.rotationPitch
                                )
                            }
                            if (tpxtimes.toInt() != 0) {
                                if(tpxtimes < 0){//判断tpxtimes是不是负数，是负数就负负得正，不是负数就正正得正，下面Z轴同样写法
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
                val thePlayer = mc.thePlayer ?: return//怎么写都warn，儍閉
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
                        //ClientUtils.displayChatMessage("2")
                        //Aim-bot ↓
                        val thePlayer = mc.thePlayer ?: return//我**你全家都**,**idea,我***

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

                        //没错这部分是脑残菜级玩家东缝西合的答辩
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


//注释

//注释

//注释 作者：菜级玩家 https://www.bilibili.com/read/cv25755876?spm_id_from=333.999.list.card_opus.click 出处：bilibili 作者：菜级玩家 https://www.bilibili.com/read/cv25755876/ 出处：bilibili