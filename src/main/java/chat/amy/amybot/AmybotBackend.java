package chat.amy.amybot;

import chat.amy.Backend;
import chat.amy.command.CommandManager;
import chat.amy.event.DiscordEventBus;
import chat.amy.queue.QueueProcessor;
import lombok.Getter;

/**
 * @author amy
 * @since 9/23/17.
 */
public class AmybotBackend implements Backend {
    @Getter
    private final QueueProcessor queueProcessor = new QueueProcessor(this, "discord-backend", 0);
    @Getter
    private final DiscordEventBus discordEventBus = new DiscordEventBus();
    
    public static void main(String[] args) {
        new AmybotBackend().start();
    }
    
    @Override
    public CommandManager getCommandManager() {
        return null;
    }
    
    private void start() {
        queueProcessor.startPolling();
    }
}
