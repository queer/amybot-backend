package chat.amy.command;

/**
 * @author amy
 * @since 9/22/17.
 */
public interface CommandManager {
    void setup();
    
    void registerCommands(Object command);
}
