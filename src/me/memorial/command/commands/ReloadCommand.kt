package me.memorial.command.commands

import me.memorial.Memorial
import me.memorial.command.Command
import me.memorial.command.CommandManager
import me.memorial.ui.client.clickgui.ClickGui
import me.memorial.ui.font.Fonts

class ReloadCommand : Command("reload", arrayOf("configreload")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        chat("Reloading...")
        chat("§c§lReloading commands...")
        Memorial.commandManager = CommandManager()
        Memorial.commandManager.registerCommands()
        Memorial.isStarting = true
        Memorial.scriptManager.disableScripts()
        Memorial.scriptManager.unloadScripts()
        for(module in Memorial.moduleManager.modules)
            Memorial.moduleManager.generateCommand(module)
        chat("§c§lReloading scripts...")
        Memorial.scriptManager.loadScripts()
        Memorial.scriptManager.enableScripts()
        chat("§c§lReloading fonts...")
        Fonts.loadFonts()
        chat("§c§lReloading modules...")
        Memorial.fileManager.loadConfig(Memorial.fileManager.modulesConfig)
        Memorial.isStarting = false
        chat("§c§lReloading values...")
        Memorial.fileManager.loadConfig(Memorial.fileManager.valuesConfig)
        chat("§c§lReloading accounts...")
        Memorial.fileManager.loadConfig(Memorial.fileManager.accountsConfig)
        chat("§c§lReloading friends...")
        Memorial.fileManager.loadConfig(Memorial.fileManager.friendsConfig)
        chat("§c§lReloading xray...")
        Memorial.fileManager.loadConfig(Memorial.fileManager.xrayConfig)
        chat("§c§lReloading ClickGUI...")
        Memorial.clickGui = ClickGui()
        Memorial.fileManager.loadConfig(Memorial.fileManager.clickGuiConfig)
        chat("Reloaded.")
    }
}
