package chat.amy.event;

import lombok.Getter;

/**
 * @author amy
 * @since 9/23/17.
 */
public abstract class EventHandler implements EventListener {
    @Getter
    private final String eventType;
    
    public EventHandler(final String eventType) {
        this.eventType = eventType;
    }
    
    @Override
    public boolean shouldRun(final WrappedEvent event) {
        return event.getType().equalsIgnoreCase(eventType);
    }
}
