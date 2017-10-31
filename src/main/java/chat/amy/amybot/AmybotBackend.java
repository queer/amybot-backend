package chat.amy.amybot;

import chat.amy.Backend;
import chat.amy.amybot.command.AmybotCommandHandler;
import chat.amy.command.CommandManager;
import chat.amy.event.DiscordEventBus;
import chat.amy.queue.QueueProcessor;
import lombok.Getter;

/**
 * @author amy
 * @since 9/23/17.
 */
 @Getter
public class AmybotBackend implements Backend {
    private final QueueProcessor queueProcessor = new QueueProcessor(this, "discord-backend", 0);
    private final DiscordEventBus discordEventBus = new DiscordEventBus();
    private final CommandManager commandManager = new AmybotCommandHandler(this);
    
    public static void main(String[] args) {
        new AmybotBackend().start();
    }
    
    private void start() {
        commandManager.setup();
        queueProcessor.startPolling();
    }
}
