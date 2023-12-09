package dev.nelly.ui

import dev.nelly.usergroups.VerifyManager
import me.memorial.ui.font.Fonts
import me.memorial.ui.font.GameFontRenderer
import me.memorial.utils.render.RenderUtils
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.renderer.texture.DynamicTexture
import java.awt.Color

class GuiLogin : GuiScreen() {
    private val font: GameFontRenderer = Fonts.font35

    companion object {
      lateinit var uuidInput: GuiTextField
    }


    override fun initGui() {
        super.initGui()

        // 创建输入框
        uuidInput = GuiTextField(0, font, width / 2 - 75, height / 2 - 10, 150, 20)
        uuidInput.setMaxStringLength(128) // 设置最大字符长度
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {

        drawLunarBackground(mouseX, mouseY, partialTicks, mc.textureManager.getDynamicTextureLocation("background", DynamicTexture(256, 256)))

        // 绘制输入框
        uuidInput.drawTextBox()

        // 绘制按钮
        RenderUtils.drawRoundedRect(width / 2F - 50F, height / 2F + 20F, 100F, 20F, 5F, Color(0,0,0,150))
        font.drawCenteredString("Login", width / 2F, height / 2F + 28F, -1)


        super.drawScreen(mouseX, mouseY, partialTicks)
    }




    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)

        // 将按键事件传递给输入框
        uuidInput.textboxKeyTyped(typedChar, keyCode)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        // 将鼠标点击事件传递给输入框
        uuidInput.mouseClicked(mouseX, mouseY, mouseButton)

        if (mouseButton == 0 && mouseX >= width / 2F - 50F && mouseX <= width / 2F + 50F &&
            mouseY >= height / 2F + 20F && mouseY <= height / 2F + 40F
        ) {
            // 点击按钮
            VerifyManager.verify2()
        }
    }

    override fun updateScreen() {
        super.updateScreen()

        // 更新输入框状态
        uuidInput.updateCursorCounter()
    }
}
