package me.memorial.events.impl.callables;

import me.memorial.events.Event;

public abstract class CancellableEvent extends Event {
    public boolean isCancelled = false;

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
    public void cancelEvent() {
        isCancelled = true;
    }
}
