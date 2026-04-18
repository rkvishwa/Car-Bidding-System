package Event;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus {

    private static final List<Consumer<Event>> listeners = new CopyOnWriteArrayList<>();

    public static void subscribe(Consumer<Event> listener) {
        listeners.add(listener);
    }

    public static void unsubscribe(Consumer<Event> listener) {
        listeners.remove(listener);
    }

    public static void publish(Event event) {
        for (Consumer<Event> l : listeners) {
            l.accept(event);
        }
    }
}