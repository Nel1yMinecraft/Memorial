package me.memorial.command.commands

import me.memorial.Memorial
import me.memorial.command.Command
import me.memorial.utils.misc.StringUtils

class ShortcutCommand: Command("shortcut", arrayOf()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        when {
            args.size > 3 && args[1].equals("add", true) -> {
                try {
                    Memorial.commandManager.registerShortcut(args[2],
                            StringUtils.toCompleteString(args, 3))

                    chat("Successfully added shortcut.")
                } catch (e: IllegalArgumentException) {
                    chat(e.message!!)
                }
            }

            args.size >= 3 && args[1].equals("remove", true) -> {
                if (Memorial.commandManager.unregisterShortcut(args[2]))
                    chat("Successfully removed shortcut.")
                else
                    chat("Shortcut does not exist.")
            }

            else -> chat("shortcut <add <shortcut_name> <script>/remove <shortcut_name>>")
        }
    }
}
