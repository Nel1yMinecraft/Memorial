package me.memorial.module

import me.memorial.Memorial
import me.memorial.events.EventTarget
import me.memorial.events.Listenable
import me.memorial.events.impl.misc.KeyEvent
import me.memorial.utils.ClientUtils
import java.util.*
import dev.dudu.ViaVersionFix
import dev.nelly.hyt.HytPacketManager
import dev.nelly.module.Disabler
import dev.nelly.module.KillSults
import dev.nelly.sk1d.module.AntiFakePlayer
import dev.nelly.sk1d.module.AutoSkyWars
import dev.nelly.sk1d.module.JumpCircle
import me.memorial.module.modules.client.FakeFPS
import me.memorial.module.modules.combat.*
import me.memorial.module.modules.render.*
import me.memorial.module.modules.exploit.*
import me.memorial.module.modules.client.HUD
import me.memorial.module.modules.client.Rotations
import me.memorial.module.modules.misc.*
import me.memorial.module.modules.movement.*
import me.memorial.module.modules.player.*
import me.memorial.module.modules.world.*
import me.memorial.module.modules.world.Timer
import net.ccbluex.liquidbounce.features.module.modules.world.ScaFull

class ModuleManager : Listenable {

    val modules = TreeSet<Module> { module1, module2 -> module1.name.compareTo(module2.name) }
    private val moduleClassMap = hashMapOf<Class<*>, Module>()

    init {
        Memorial.eventManager.registerListener(this)
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        ClientUtils.getLogger().info("[ModuleManager] Loading modules...")

        registerModules(
            KillSults::class.java,
            AutoArmor::class.java,
            AutoLeave::class.java,
            AutoPot::class.java,
            AutoSoup::class.java,
            AutoWeapon::class.java,
            ViaVersionFix::class.java,
            ScaFull::class.java,
            Criticals::class.java,
            KillAura::class.java,
            Velocity::class.java,
            Fly::class.java,
            ClickGUI::class.java,
            HighJump::class.java,
            InventoryMove::class.java,
            NoSlow::class.java,
            SafeWalk::class.java,
            WallClimb::class.java,
            Strafe::class.java,
            Sprint::class.java,
            Teams::class.java,
            NoRotateSet::class.java,
            AntiBot::class.java,
            ChestStealer::class.java,
            Scaffold::class.java,
            CivBreak::class.java,
            Tower::class.java,
            FastBreak::class.java,
            FastPlace::class.java,
            ESP::class.java,
            Speed::class.java,
            Tracers::class.java,
            NameTags::class.java,
            FastUse::class.java,
            Teleport::class.java,
            Fullbright::class.java,
            ItemESP::class.java,
            StorageESP::class.java,
            Projectiles::class.java,
            NoClip::class.java,
            Nuker::class.java,
            PingSpoof::class.java,
            Step::class.java,
            AutoRespawn::class.java,
            AutoTool::class.java,
            NoWeb::class.java,
            Spammer::class.java,
            IceSpeed::class.java,
            Zoot::class.java,
            Regen::class.java,
            NoFall::class.java,
            Blink::class.java,
            NameProtect::class.java,
            NoHurtCam::class.java,
            Ghost::class.java,
            MidClick::class.java,
            XRay::class.java,
            Timer::class.java,
            Sneak::class.java,
            Paralyze::class.java,
            AutoBreak::class.java,
            FreeCam::class.java,
            Eagle::class.java,
            HitBox::class.java,
            AntiCactus::class.java,
            Plugins::class.java,
            LongJump::class.java,
            Parkour::class.java,
            LadderJump::class.java,
            MultiActions::class.java,
            AutoClicker::class.java,
            NoBob::class.java,
            BlockOverlay::class.java,
            BlockESP::class.java,
            Chams::class.java,
            Clip::class.java,
            Phase::class.java,
            ServerCrasher::class.java,
            NoFOV::class.java,
            SwingAnimation::class.java,
            ReverseStep::class.java,
            InventoryCleaner::class.java,
            TrueSight::class.java,
            AntiBlind::class.java,
            NoSwing::class.java,
            Breadcrumbs::class.java,
            AbortBreaking::class.java,
            PotionSaver::class.java,
            CameraClip::class.java,
            WaterSpeed::class.java,
            SlimeJump::class.java,
            MoreCarry::class.java,
            NoPitchLimit::class.java,
            Kick::class.java,
            Liquids::class.java,
            AtAllProvider::class.java,
            SuperKnockback::class.java,
            ProphuntESP::class.java,
            AutoFish::class.java,
            Damage::class.java,
            KeepContainer::class.java,
            VehicleOneHit::class.java,
            Reach::class.java,
            Rotations::class.java,
            NoJumpDelay::class.java,
            AntiAFK::class.java,
            PerfectHorseJump::class.java,
            HUD::class.java,
            TNTESP::class.java,
            ComponentOnHover::class.java,
            KeepAlive::class.java,
            ResourcePackSpoof::class.java,
            NoSlowBreak::class.java,
            PortalMenu::class.java,
            ColorMixer::class.java,
            AntiFakePlayer::class.java,
            JumpCircle::class.java,
            AutoSkyWars::class.java,
            HytPacketManager::class.java,
            KillSults::class.java,
            ChestAura::class.java,
            Disabler::class.java
        )

        registerModule(NoScoreboard)
        registerModule(Fucker)

        ClientUtils.getLogger().info("[ModuleManager] Loaded ${modules.size} modules.")

    }



    fun getModuleInCategory(category: ModuleCategory) = modules.filter { it.category == category }

    /**
     * Register [module]
     */
    fun registerModule(module: Module) {
        modules += module
        moduleClassMap[module.javaClass] = module

        generateCommand(module)
        Memorial.eventManager.registerListener(module)
    }

    /**
     * Register [moduleClass]
     */
    private fun registerModule(moduleClass: Class<out Module>) {
        try {
            registerModule(moduleClass.newInstance())
        } catch (e: Throwable) {
            ClientUtils.getLogger().error("Failed to load module: ${moduleClass.name} (${e.javaClass.name}: ${e.message})")
        }
    }

    /**
     * Register a list of modules
     */
    @SafeVarargs
    fun registerModules(vararg modules: Class<out Module>) {
        modules.forEach(this::registerModule)
    }

    /**
     * Unregister module
     */
    fun unregisterModule(module: Module) {
        modules.remove(module)
        moduleClassMap.remove(module::class.java)
        Memorial.eventManager.unregisterListener(module)
    }

    /**
     * Generate command for [module]
     */
    internal fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        Memorial.commandManager.registerCommand(ModuleCommand(module, values))
    }

    /**
     * Legacy stuff
     *
     * TODO: Remove later when everything is translated to Kotlin
     */

    /**
     * Get module by [moduleClass]
     */
    fun getModule(moduleClass: Class<*>) = moduleClassMap[moduleClass]

    operator fun get(clazz: Class<*>) = getModule(clazz)

    /**
     * Get module by [moduleName]
     */
    fun getModule(moduleName: String?) = modules.find { it.name.equals(moduleName, ignoreCase = true) }

    /**
     * Module related events
     */

    /**
     * Handle incoming key presses
     */
    @EventTarget
    private fun onKey(event: KeyEvent) = modules.filter { it.keyBind == event.key }.forEach { it.toggle() }

    override fun handleEvents() = true
}
