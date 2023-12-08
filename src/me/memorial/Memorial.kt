package me.memorial

import dev.nelly.ui.GuiLogin
import dev.nelly.viamcp.ViaMCP
import me.memorial.cape.CapeAPI.registerCapeService
import me.memorial.command.CommandManager
import me.memorial.events.impl.misc.ClientShutdownEvent
import me.memorial.events.EventManager
import me.memorial.file.FileManager
import me.memorial.module.ModuleManager
import me.memorial.module.modules.client.test
import me.memorial.script.ScriptManager
import me.memorial.special.AntiForge
import me.memorial.special.BungeeCordSpoof
import me.memorial.special.DonatorCape
import me.memorial.tabs.BlocksTab
import me.memorial.tabs.ExploitsTab
import me.memorial.tabs.HeadsTab
import me.memorial.ui.client.GuiMainMenu
import me.memorial.ui.client.altmanager.GuiAltManager
import me.memorial.ui.client.clickgui.ClickGui
import me.memorial.ui.client.lunar.ui.MainMenu
import me.memorial.ui.font.Fonts
import me.memorial.utils.*
import me.memorial.utils.ClassUtils.hasForge
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon

object Memorial {

    // Client information
    const val CLIENT_NAME = "Memorial"
    const val CLIENT_VERSION = 1.0
    const val CLIENT_CREATOR = "Memorial-Team || CCBlueX"
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager

    // ClickGUI
    lateinit var clickGui: ClickGui

    // Update information
    var latestVersion = 0

    // Menu Background
    var background: ResourceLocation? = null

    lateinit var guiMain: GuiScreen

    @JvmStatic
    fun showNotification(title: String, text: String, type: TrayIcon.MessageType?) {
        val tray = SystemTray.getSystemTray()
        val image = Toolkit.getDefaultToolkit().createImage("icon.png")
        val trayIcon = TrayIcon(image, "Tray Demo")
        trayIcon.isImageAutoSize = true
        trayIcon.toolTip = "System tray icon demo"
        tray.add(trayIcon)
        trayIcon.displayMessage(title, text, type)
    }

    fun showNotification(title: String, text: String) {
        showNotification(title,text, TrayIcon.MessageType.INFO)
    }

    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true

        if (!System.getProperty("os.name").lowercase().contains("win")) {
            MinecraftInstance.mc.shutdown()
        }

        ClientUtils.loginfo("Starting $CLIENT_NAME b$CLIENT_VERSION, by $CLIENT_CREATOR")

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
        eventManager.registerListener(InventoryUtils2())
        eventManager.registerListener(InventoryHelper)

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


        // Disable optifine fastrender
        ClientUtils.disableFastRender()

        // Load generators
        GuiAltManager.loadGenerators()

        //guiMain = GuiMainMenu()
        guiMain = GuiLogin()

        try {
            ClientUtils.loginfo("Starting ViaMCP...")
            val viaMCP = ViaMCP.getInstance()
            viaMCP.start()
            viaMCP.initAsyncSlider(100, 100, 110, 20)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Set is starting status
        isStarting = false
    }

    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        // Call client shutdown
        eventManager.callEvent(ClientShutdownEvent())

        // Save all available configs
        fileManager.saveAllConfigs()

    }
}