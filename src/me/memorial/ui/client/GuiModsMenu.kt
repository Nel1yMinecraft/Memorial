package me.memorial.ui.client

import me.memorial.ui.font.Fonts
import me.memorial.utils.ClientUtils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard

class GuiModsMenu(private val prevGui: GuiScreen) : GuiScreen() {

    override fun initGui() {
        buttonList.add(GuiButton(0, width / 2 - 100, height / 4 + 48, "Forge Mods"))
        buttonList.add(GuiButton(1, width / 2 - 100, height / 4 + 48 + 25, "Scripts"))
        buttonList.add(GuiButton(2, width / 2 - 100, height / 4 + 48 + 50, "Back"))
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> me.memorial.utils.ClientUtils.loginfo("tf")
            1 -> mc.displayGuiScreen(GuiScripts(this))
            2 -> mc.displayGuiScreen(prevGui)
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)

        Fonts.fontBold180.drawCenteredString("Mods", this.width / 2F, height / 8F + 5F, 4673984, true)

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui)
            return
        }

        super.keyTyped(typedChar, keyCode)
    }
}