package me.memorial.events.impl.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.memorial.events.Event;
@Getter
@AllArgsConstructor
public class KeyEvent extends Event {
    public int key;
}
