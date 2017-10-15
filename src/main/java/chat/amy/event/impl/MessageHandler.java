package chat.amy.event.impl;

import chat.amy.event.EventHandler;
import chat.amy.event.WrappedEvent;

/**
 * @author amy
 * @since 9/23/17.
 */
public abstract class MessageHandler extends EventHandler {
    public MessageHandler() {
        super("MESSAGE_CREATE");
    }
    
    @Override
    public boolean shouldRun(final WrappedEvent event) {
        return super.shouldRun(event) && event.getData().getInt("type") == 0;
    }
}
