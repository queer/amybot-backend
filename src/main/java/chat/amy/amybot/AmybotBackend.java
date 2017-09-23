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
public class AmybotBackend implements Backend {
    @Getter
    private final QueueProcessor queueProcessor = new QueueProcessor(this, "discord-backend", 0);
    @Getter
    private final DiscordEventBus discordEventBus = new DiscordEventBus();
    @Getter
    private final CommandManager commandManager = new AmybotCommandHandler(this);
    
    public static void main(String[] args) {
        new AmybotBackend().start();
    }
    
    private void start() {
        commandManager.setup();
        queueProcessor.startPolling();
    }
}
