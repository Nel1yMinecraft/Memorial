package me.memorial.command.commands

import me.memorial.command.Command

class PingCommand : Command("ping", emptyArray()) {
    override fun execute(args: Array<String>) {
        chat("§3Your ping is §a${mc.netHandler.getPlayerInfo(mc.thePlayer.uniqueID).responseTime}ms§3.")
    }
}