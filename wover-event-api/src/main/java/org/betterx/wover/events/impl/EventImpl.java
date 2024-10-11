package org.betterx.wover.events.impl;

import org.betterx.wover.entrypoint.WoverEvents;
import org.betterx.wover.events.api.Subscriber;

import java.util.function.Consumer;

public class EventImpl<T extends Subscriber> extends AbstractEvent<T> {
    public EventImpl(String eventName) {
        super(eventName);
    }

    public void emit(Consumer<T> c) {
        WoverEvents.C.LOG.debug("Emitting event: " + eventName);
        handlers.forEach(sub -> c.accept(sub.task));
    }
}
