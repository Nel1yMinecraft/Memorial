package dev.nelly.ui.guilogin

import me.memorial.ui.font.GameFontRenderer
import me.memorial.utils.render.RenderUtils
import java.awt.Color
import java.awt.TextField

class Input(val x: Float,val y: Float,val width: Float, val height: Float,val radius: Float,val font: GameFontRenderer) {

   private val textField = TextField()

    var isFocused = false
        set(value) {
            field = value
            if (value) {
                textField.requestFocus()
            }
        }

    fun update(mouseX: Int, mouseY: Int, isMousePressed: Boolean) {
        // 判断鼠标是否在输入框内
        if (isMouseOverInputBox(mouseX, mouseY)) {
            // 如果左键被按下，则将输入框设置为焦点状态
            if (isMousePressed) {
                isFocused = true
            }
        } else {
            // 如果鼠标不在输入框内，则取消焦点状态
            if (isMousePressed) {
                isFocused = false
            }
        }
    }

    // radius 0F - 10F
    fun drawInput() {
        // 绘制输入框的边框
        val borderColor = if (isFocused) Color.BLUE else Color.BLACK
        RenderUtils.drawRoundedRect(x, y, width, height,radius, borderColor)

        // 绘制输入框的背景
        val bgColor = if (isFocused) Color.WHITE else Color(240, 240, 240)
        RenderUtils.drawRoundedRect(x + 1, y + 1, width - 2, height - 2,radius, bgColor)

        // 绘制输入框中的文本
        val textX = x + 4
        val textY = y + height / 2 + 5
        font.drawString(textField.text, textX, textY, Color.BLACK.rgb)
    }

   fun isMouseOverInputBox(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
    }

}
