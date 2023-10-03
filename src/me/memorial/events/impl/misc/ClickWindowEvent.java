package me.memorial.events.impl.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.memorial.events.impl.callables.CancellableEvent;

@Getter
@AllArgsConstructor
public class ClickWindowEvent extends CancellableEvent {
    public int windowId,slotId,mouseButtonClicked,mode;
}
