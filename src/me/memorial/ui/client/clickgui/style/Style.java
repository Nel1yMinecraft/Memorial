package me.memorial.ui.client.clickgui.style;

import me.memorial.ui.client.clickgui.Panel;
import me.memorial.ui.client.clickgui.elements.ButtonElement;
import me.memorial.ui.client.clickgui.elements.ModuleElement;
import me.memorial.utils.MinecraftInstance;

public abstract class Style extends MinecraftInstance {

    public abstract void drawPanel(final int mouseX, final int mouseY, final Panel panel);

    public abstract void drawDescription(final int mouseX, final int mouseY, final String text);

    public abstract void drawButtonElement(final int mouseX, final int mouseY, final ButtonElement buttonElement);

    public abstract void drawModuleElement(final int mouseX, final int mouseY, final ModuleElement moduleElement);

}
