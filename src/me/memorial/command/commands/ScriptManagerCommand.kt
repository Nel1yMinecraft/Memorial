package me.memorial.command.commands

import me.memorial.Memorial
import me.memorial.command.Command
import me.memorial.command.CommandManager
import me.memorial.ui.client.clickgui.ClickGui
import me.memorial.utils.ClientUtils
import me.memorial.utils.misc.MiscUtils
import org.apache.commons.io.IOUtils
import java.awt.Desktop
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.zip.ZipFile

class ScriptManagerCommand : Command("scriptmanager", arrayOf("scripts")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            when {
                args[1].equals("import", true) -> {
                    try {
                        val file = MiscUtils.openFileChooser() ?: return
                        val fileName = file.name

                        if (fileName.endsWith(".js")) {
                            Memorial.scriptManager.importScript(file)

                            Memorial.clickGui = ClickGui()
                            Memorial.fileManager.loadConfig(Memorial.fileManager.clickGuiConfig)

                            chat("Successfully imported script.")
                            return
                        } else if (fileName.endsWith(".zip")) {
                            val zipFile = ZipFile(file)
                            val entries = zipFile.entries()
                            val scriptFiles = ArrayList<File>()

                            while (entries.hasMoreElements()) {
                                val entry = entries.nextElement()
                                val entryName = entry.name
                                val entryFile = File(Memorial.scriptManager.scriptsFolder, entryName)

                                if (entry.isDirectory) {
                                    entryFile.mkdir()
                                    continue
                                }

                                val fileStream = zipFile.getInputStream(entry)
                                val fileOutputStream = FileOutputStream(entryFile)

                                IOUtils.copy(fileStream, fileOutputStream)
                                fileOutputStream.close()
                                fileStream.close()

                                if (!entryName.contains("/"))
                                    scriptFiles.add(entryFile)
                            }

                            scriptFiles.forEach { scriptFile -> Memorial.scriptManager.loadScript(scriptFile) }

                            Memorial.clickGui = ClickGui()
                            Memorial.fileManager.loadConfig(Memorial.fileManager.clickGuiConfig)
                            Memorial.fileManager.loadConfig(Memorial.fileManager.hudConfig)

                            chat("Successfully imported script.")
                            return
                        }

                        chat("The file extension has to be .js or .zip")
                    } catch (t: Throwable) {
                        ClientUtils.getLogger().error("Something went wrong while importing a script.", t)
                        chat("${t.javaClass.name}: ${t.message}")
                    }
                }

                args[1].equals("delete", true) -> {
                    try {
                        if (args.size <= 2) {
                            chatSyntax("scriptmanager delete <index>")
                            return
                        }

                        val scriptIndex = args[2].toInt()
                        val scripts = Memorial.scriptManager.scripts

                        if (scriptIndex >= scripts.size) {
                            chat("Index $scriptIndex is too high.")
                            return
                        }

                        val script = scripts[scriptIndex]

                        Memorial.scriptManager.deleteScript(script)

                        Memorial.clickGui = ClickGui()
                        Memorial.fileManager.loadConfig(Memorial.fileManager.clickGuiConfig)
                        Memorial.fileManager.loadConfig(Memorial.fileManager.hudConfig)
                        chat("Successfully deleted script.")
                    } catch (numberFormat: NumberFormatException) {
                        chatSyntaxError()
                    } catch (t: Throwable) {
                        ClientUtils.getLogger().error("Something went wrong while deleting a script.", t)
                        chat("${t.javaClass.name}: ${t.message}")
                    }
                }

                args[1].equals("reload", true) -> {
                    try {
                        Memorial.commandManager = CommandManager()
                        Memorial.commandManager.registerCommands()
                        Memorial.isStarting = true
                        Memorial.scriptManager.disableScripts()
                        Memorial.scriptManager.unloadScripts()
                        for(module in Memorial.moduleManager.modules)
                            Memorial.moduleManager.generateCommand(module)
                        Memorial.scriptManager.loadScripts()
                        Memorial.scriptManager.enableScripts()
                        Memorial.fileManager.loadConfig(Memorial.fileManager.modulesConfig)
                        Memorial.isStarting = false
                        Memorial.fileManager.loadConfig(Memorial.fileManager.valuesConfig)
                        Memorial.clickGui = ClickGui()
                        Memorial.fileManager.loadConfig(Memorial.fileManager.clickGuiConfig)
                        chat("Successfully reloaded all scripts.")
                    } catch (t: Throwable) {
                        ClientUtils.getLogger().error("Something went wrong while reloading all scripts.", t)
                        chat("${t.javaClass.name}: ${t.message}")
                    }
                }

                args[1].equals("folder", true) -> {
                    try {
                        Desktop.getDesktop().open(Memorial.scriptManager.scriptsFolder)
                        chat("Successfully opened scripts folder.")
                    } catch (t: Throwable) {
                        ClientUtils.getLogger().error("Something went wrong while trying to open your scripts folder.", t)
                        chat("${t.javaClass.name}: ${t.message}")
                    }
                }
            }

            return
        }

        val scriptManager = Memorial.scriptManager

        if (scriptManager.scripts.isNotEmpty()) {
            chat("§c§lScripts")
            scriptManager.scripts.forEachIndexed { index, script -> chat("$index: §a§l${script.scriptName} §a§lv${script.scriptVersion} §3by §a§l${script.scriptAuthors.joinToString(", ")}") }
        }

        chatSyntax("scriptmanager <import/delete/reload/folder>")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> listOf("delete", "import", "folder", "reload")
                .filter { it.startsWith(args[0], true) }
            else -> emptyList()
        }
    }
}