package me.memorial.events.impl.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.memorial.events.Event;
import net.minecraft.client.gui.GuiScreen;

@Getter
@AllArgsConstructor
public class ScreenEvent extends Event {
    public GuiScreen guiScreen;
}
