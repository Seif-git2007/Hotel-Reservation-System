import javafx.application.Platform;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {

    public enum Event {
        RESERVATION_CHANGED,
        USER_CHANGED,
        ROOM_CHANGED,
        ROOMTYPE_CHANGED,
        AMENITY_CHANGED,
        INVOICE_CHANGED
    }

    private static final Map<Event, List<Runnable>> listeners = new ConcurrentHashMap<>();

    public static void subscribe(Event event, Runnable listener) {
        listeners.computeIfAbsent(event, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public static void unsubscribe(Event event, Runnable listener) {
        List<Runnable> list = listeners.get(event);
        if (list != null) list.remove(listener);
    }

    public static void fire(Event event) {
        List<Runnable> list = listeners.get(event);
        if (list == null) return;
        for (Runnable r : list) Platform.runLater(r);
    }
}