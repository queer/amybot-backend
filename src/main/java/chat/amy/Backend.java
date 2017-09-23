package chat.amy;

import chat.amy.command.CommandManager;
import chat.amy.event.DiscordEventBus;
import chat.amy.queue.QueueProcessor;

/**
 * This is intentionally NOT a main class. Your "plugin host" will need to
 * provide an actual main class and run everything. You can think of this as
 * more of a base library than an actual "application server"-type thing.
 * <p>
 * This class just defines a generic "utility" interface that you probably want
 * to implement if you really want to roll your own.
 *
 * @author amy
 * @since 9/22/17.
 */
public interface Backend {
    CommandManager getCommandManager();
    
    DiscordEventBus getDiscordEventBus();
    
    QueueProcessor getQueueProcessor();
}
