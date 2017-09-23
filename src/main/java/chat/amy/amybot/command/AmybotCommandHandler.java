package chat.amy.amybot.command;

import chat.amy.Backend;
import chat.amy.amybot.command.Command;
import chat.amy.command.CommandManager;
import chat.amy.event.EventHandler;
import chat.amy.event.WrappedEvent;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author amy
 * @since 9/23/17.
 */
public class AmybotCommandHandler implements CommandManager {
    private final Map<String, CommandWrapper> commandMap = new ConcurrentHashMap<>();
    
    private final Backend backend;
    
    public AmybotCommandHandler(final Backend backend) {
        this.backend = backend;
    }
    
    @Override
    public void setup() {
        backend.getDiscordEventBus().register(new EventHandler("MESSAGE_CREATE") {
            @Override
            public void onEvent(final WrappedEvent event) {
            
            }
        });
    }
    
    @Override
    public final void registerCommands(final Object source) {
        final Class<?> clz = source.getClass();
        final Method[] methods = clz.getDeclaredMethods();
        for(final Method method : methods) {
            if(method.isAnnotationPresent(Command.class)) {
                if((method.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
                    throw new IllegalArgumentException("Can't register non-public command method: " + clz.getName()
                            + '#' + method.getName());
                }
                if(method.getParameterTypes().length != 1) {
                    throw new IllegalArgumentException(String.format("Method %s must take a single argument!",
                            clz.getName() + '#' + method.getName()));
                }
                if(!method.getParameterTypes()[0].equals(CommandState.class)) {
                    throw new IllegalArgumentException(String.format("Method %s must take a CommandState as an argument!",
                            clz.getName() + '#' + method.getName()));
                }
                if(method.getParameterCount() > 1) {
                    throw new IllegalArgumentException(String.format("Method %s must take exactly 1 argument!",
                            clz.getName() + '#' + method.getName()));
                }
                if(!method.getReturnType().equals(Void.TYPE)) {
                    throw new IllegalArgumentException(String.format("Method %s must return void!",
                            clz.getName() + '#' + method.getName()));
                }
                final Command command = method.getAnnotation(Command.class);
                commandMap.put(command.name(), new CommandWrapper(command.name(), command.desc(), source, method));
            }
        }
    }
    
    @Value
    @RequiredArgsConstructor
    public static final class CommandState {
        private final String commandName;
        private final List<String> args;
        private final WrappedEvent source;
    }
    
    @Value
    @RequiredArgsConstructor
    private final class CommandWrapper {
        private final String name;
        private final String desc;
        private final Object source;
        private final Method method;
    }
}
