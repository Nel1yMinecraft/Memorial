package me.memorial.module

import java.awt.Color

enum class ModuleCategory(val displayName: String,val color: Int) {

    COMBAT("Combat", Color(219, 120, 163).rgb),
    PLAYER("Player", Color(224, 197, 242).rgb),
    MOVEMENT("Movement", Color(91, 153, 204).rgb),
    RENDER("Render", Color(255, 187, 145).rgb),
    WORLD("World", Color(196, 224, 249).rgb),
    CLIENT("CLIENT",Color(196, 224, 249).rgb),
    SCRIPT("Script",Color(0, 0, 0).rgb),
    MISC("Misc",Color(0, 0, 0).rgb),
    EXPLOIT("Exploit",Color(0, 0, 0).rgb);

    @JvmName("getColor1")
    fun getColor(): Int {
        return this.color
    }
}