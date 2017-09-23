package chat.amy.message;

import chat.amy.event.WrappedEvent;

/**
 * Generic message processor. May delegate out to a {@link chat.amy.command.CommandManager}
 * if needed.
 * <p>
 * Note that presently, this is only designed for use with Discord.
 *
 * @author amy
 * @since 9/23/17.
 */
public interface MessageProcessor {
    default boolean validate(WrappedEvent event) {
        // Discord sent a MESSAGE_CREATE event
        return event.getType().equalsIgnoreCase("MESSAGE_CREATE");
    }
    
    void process(WrappedEvent event);
}
