package chat.amy.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author amy
 * @since 9/23/17.
 */
public class DiscordEventBus {
    private final List<EventListener> handlers = new CopyOnWriteArrayList<>();
    
    public DiscordEventBus() {
    }
    
    public void register(final EventListener handler) {
        if(!handlers.contains(handler)) {
            handlers.add(handler);
        }
    }
    
    public void push(final WrappedEvent event) {
        handlers.stream().filter(e -> e.shouldRun(event)).forEach(e -> e.onEvent(event));
    }
    
    public void clear() {
        handlers.clear();
    }
}
