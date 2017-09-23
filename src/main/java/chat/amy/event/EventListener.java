package chat.amy.event;

/**
 * @author amy
 * @since 9/23/17.
 */
public interface EventListener {
    String getEventType();
    
    boolean shouldRun(WrappedEvent event);
    
    void onEvent(WrappedEvent event);
}
