package me.memorial.events.impl.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.memorial.events.Event;

@Getter
@Setter
public class TextEvent extends Event {
    public String text;

    public TextEvent(String text) {
        this.text = text;
    }
}
