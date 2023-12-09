package dev.nelly

import org.lwjgl.input.Keyboard

object InputFix {

     fun inputPatch(): Boolean {
        return Keyboard.getEventKeyState() || Keyboard.getEventKey() === 0 && Character.isDefined(Keyboard.getEventCharacter())
    }

}