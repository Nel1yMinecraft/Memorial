package dev.nelly.ui.guilogin

import me.memorial.ui.font.GameFontRenderer
import me.memorial.utils.render.RenderUtils
import java.awt.Color

class Button(val buttonId: Int, var mouseX: Float,
             var mouseY: Float, val text: String, val font: GameFontRenderer, val x: Float, val y: Float, val width: Float, val height: Float, val radius: Float, val color: Color) {

    var pressButtonId: Int = -1

    // radius 0F - 10F
    fun drawButton() {
        if (isMouseOverButton(x, y, width, height, mouseX, mouseY)) {
            RenderUtils.drawRoundedRect(x, y, width, height, radius, color.brighter())
            if (isMousePressed()) {
                pressButtonId = buttonId
            }
        } else {
            RenderUtils.drawRoundedRect(x, y, width, height, radius, color)
        }
        if (isMouseReleased()) {
            pressButtonId = -1
        }

        val textX = x + 4
        val textY = y + height / 2 + 5
        font.drawString(text, textX, textY, Color.BLACK.rgb)
    }

    fun isMouseOverButton(x: Float, y: Float, width: Float, height: Float, mouseX: Float, mouseY: Float): Boolean {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
    }

    private fun isMousePressed(): Boolean {
        return pressButtonId == -1
    }

    private fun isMouseReleased(): Boolean {
        return pressButtonId != -1
    }

}
