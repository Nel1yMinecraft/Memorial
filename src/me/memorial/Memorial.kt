package me.memorial

//import net.ccbluex.liquidbounce.ui.client.lunar.ui.MainMenu
import me.memorial.cape.CapeAPI.registerCapeService
import me.memorial.events.ClientShutdownEvent
import me.memorial.events.EventManager
import me.memorial.command.CommandManager
import me.memorial.module.ModuleManager
import me.memorial.special.AntiForge
import me.memorial.special.BungeeCordSpoof
import me.memorial.special.DonatorCape
import me.memorial.file.FileManager
import me.memorial.script.ScriptManager
import me.memorial.tabs.BlocksTab
import me.memorial.tabs.ExploitsTab
import me.memorial.tabs.HeadsTab
import me.memorial.ui.client.GuiMainMenu
import me.memorial.ui.client.altmanager.GuiAltManager
import me.memorial.ui.client.clickgui.ClickGui
import me.memorial.ui.client.hud.HUD
import me.memorial.ui.client.hud.HUD.Companion.createDefault
import me.memorial.ui.font.Fonts
import me.memorial.utils.ClassUtils.hasForge
import me.memorial.utils.ClientUtils
import me.memorial.utils.InventoryUtils
import me.memorial.utils.RotationUtils
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation

object Memorial {

    // Client information
    const val CLIENT_NAME = "LiquidBounce"
    const val CLIENT_VERSION = 73
    const val IN_DEV = true
    const val CLIENT_CREATOR = "CCBlueX"
    const val MINECRAFT_VERSION = "1.8.9"
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"//finish

    var isStarting = false//finish

    // Managers
    lateinit var moduleManager: ModuleManager//finish
    lateinit var commandManager: CommandManager//finish
    lateinit var eventManager: EventManager//finish
    lateinit var fileManager: FileManager//finish
    lateinit var scriptManager: ScriptManager//finish

    // HUD & ClickGUI
    lateinit var hud: HUD//finish

    lateinit var clickGui: ClickGui//finish

    // Update information
    var latestVersion = 0//finish

    // Menu Background
    var background: ResourceLocation? = null//finish

    lateinit var guiMain: GuiScreen//finish

    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true

        ClientUtils.getLogger().info("Starting $CLIENT_NAME b$CLIENT_VERSION, by $CLIENT_CREATOR")

        // Create file manager
        fileManager = FileManager()

        // Crate event manager
        eventManager = EventManager()

        // Register listeners
        eventManager.registerListener(RotationUtils())
        eventManager.registerListener(AntiForge())
        eventManager.registerListener(BungeeCordSpoof())
        eventManager.registerListener(DonatorCape())
        eventManager.registerListener(InventoryUtils())

        // Create command manager
        commandManager = CommandManager()

        // Load client fonts
        Fonts.loadFonts()

        // Setup module manager and register modules
        moduleManager = ModuleManager()
        moduleManager.registerModules()

        // Remapper
        try {
            // ScriptManager
            scriptManager = ScriptManager()
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to load scripts.", throwable)
        }

        // Register commands
        commandManager.registerCommands()

        // Load configs
        fileManager.loadConfigs(
            fileManager.modulesConfig, fileManager.valuesConfig, fileManager.accountsConfig,
                fileManager.friendsConfig, fileManager.xrayConfig, fileManager.shortcutsConfig)

        // ClickGUI
        clickGui = ClickGui()
        fileManager.loadConfig(fileManager.clickGuiConfig)

        // Tabs (Only for Forge!)
        if (hasForge()) {
            BlocksTab()
            ExploitsTab()
            HeadsTab()
        }

        // Register capes service
        try {
            registerCapeService()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to register cape service", throwable)
        }

        // Set HUD
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)

        // Disable optifine fastrender
        ClientUtils.disableFastRender()

        // Load generators
        GuiAltManager.loadGenerators()

        guiMain = GuiMainMenu()
        //guiMain = MainMenu()

        // Set is starting status
        isStarting = false
    }

    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        // Call client shutdown
        eventManager.callEvent(ClientShutdownEvent()) // pass

        // Save all available configs
        fileManager.saveAllConfigs()

    }
}