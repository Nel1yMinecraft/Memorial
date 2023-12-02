package dev.nelly.hyt.menu;

import java.awt.Color;

import dev.nelly.hyt.menu.Screen;
import me.memorial.utils.MouseUtils;
import me.memorial.ui.font.Fonts;
import me.memorial.utils.animations.Direction;
import me.memorial.utils.animations.impl.DecelerateAnimation;
import me.memorial.utils.render.RenderUtils;

public class MenuButton implements Screen {
    public final String text;
    private DecelerateAnimation hoverAnimation;
    public float x;
    public float y;
    public float width;
    public float height;
    public Runnable clickAction;

    public MenuButton(String text) {
        this.text = text;
    }

    public void initGui() {
        this.hoverAnimation = new DecelerateAnimation(500, 1.0);
    }

    public void keyTyped(char typedChar, int keyCode) {
    }

    public void drawScreen(int mouseX, int mouseY) {
        boolean hovered = MouseUtils.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        this.hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        RenderUtils.drawRect(this.x, this.y, this.width, this.height, new Color(30, 30, 30, 100));
        Fonts.font20.drawCenteredString(this.text, this.x + this.width / 2.0F, this.y + Fonts.font20.getMiddleOfBox(this.height) + 2.0F, -1);
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = MouseUtils.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        if (hovered) {
            this.clickAction.run();
        }

    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
    }
}
