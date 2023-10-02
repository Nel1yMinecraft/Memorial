package me.memorial.command.shortcuts

import me.memorial.command.Command

class Shortcut(val name: String, val script: List<Pair<Command, Array<String>>>): Command(name, arrayOf()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        script.forEach { it.first.execute(it.second) }
    }
}
