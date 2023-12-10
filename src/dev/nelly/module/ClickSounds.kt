package dev.nelly.module

import me.memorial.events.EventTarget
import me.memorial.events.impl.player.UpdateEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.value.ListValue
import net.minecraft.util.ResourceLocation
import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip


@ModuleInfo(name = "ClickSounds", description = "clicksounds", category = ModuleCategory.MISC)
class ClickSounds : Module() {
    private val sound: ListValue = ListValue("Sound", arrayOf("Standard", "Double", "Alan"), "Standard")

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        var soundName = "Standard"
        when (sound.get()) {
            "Double" -> {
                soundName = "Double"
            }

            "Alan" -> {
                soundName = "Alan"
            }
        }
        val file = File("${this.javaClass.`package`}/$soundName.ogg")
        val audioIn = AudioSystem.getAudioInputStream(file)
        val clip: Clip = AudioSystem.getClip()
        clip.open(audioIn)
        clip.start()

    }


}