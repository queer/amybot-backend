package chat.amy.discord;

import lombok.Getter;
import lombok.ToString;
import lombok.Value;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.requests.Request;
import net.dv8tion.jda.core.requests.Response;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.Route;
import net.dv8tion.jda.core.requests.Route.CompiledRoute;
import okhttp3.OkHttpClient.Builder;
import org.json.JSONObject;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * "Fake" JDA used for sending REST requests. NOTE: This class does NOT do any
 * validation of input. You're on your own.
 *
 * @author amy
 * @since 9/23/17.
 */
@Getter
public class FakeJDA {
    private final Map<String, Route> routeStorage = new ConcurrentHashMap<>();
    private final JDAImpl jda;
    
    @SuppressWarnings("UnnecessarilyQualifiedInnerClassAccess")
    public FakeJDA(final JedisPool pool, final String token) {
        jda = new JDAImpl(AccountType.BOT, new Builder(), null, new RedisRatelimiter(pool), false, false,
                false, true, 8, 900);
        jda.setToken(token);
        
        final Collection<Field> routes = new ArrayList<>();
        routes.addAll(Arrays.asList(Route.Misc.class.getDeclaredFields()));
        routes.addAll(Arrays.asList(Route.Applications.class.getDeclaredFields()));
        routes.addAll(Arrays.asList(Route.Self.class.getDeclaredFields()));
        routes.addAll(Arrays.asList(Route.Users.class.getDeclaredFields()));
        routes.addAll(Arrays.asList(Route.Relationships.class.getDeclaredFields()));
        routes.addAll(Arrays.asList(Route.Guilds.class.getDeclaredFields()));
        routes.addAll(Arrays.asList(Route.Emotes.class.getDeclaredFields()));
        routes.addAll(Arrays.asList(Route.Webhooks.class.getDeclaredFields()));
        routes.addAll(Arrays.asList(Route.Roles.class.getDeclaredFields()));
        routes.addAll(Arrays.asList(Route.Channels.class.getDeclaredFields()));
        routes.addAll(Arrays.asList(Route.Messages.class.getDeclaredFields()));
        routes.addAll(Arrays.asList(Route.Invites.class.getDeclaredFields()));
        routes.addAll(Arrays.asList(Route.Custom.class.getDeclaredFields()));
        routes.forEach(e -> {
            if(Modifier.isPublic(e.getModifiers()) && Modifier.isStatic(e.getModifiers())) {
                try {
                    routeStorage.put(e.getName(), (Route) e.get(null));
                } catch(final IllegalAccessException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
    
    // TODO: What about stuff that doesn't need a payload?
    public RestAction<JSONObject> queue(final RESTObject object) {
        final Route route = object.getRoute();
        final String[] routeParams = object.getRouteParams();
        final JSONObject payload = object.getPayload();
        final CompiledRoute compiledRoute = route.compile(routeParams);
        
        return new RestAction<JSONObject>(jda, compiledRoute, payload) {
            @Override
            protected void handleResponse(final Response response, final Request<JSONObject> request) {
                // TODO
                if(response.isOk()) {
                    request.onSuccess(response.getObject());
                } else {
                    request.onFailure(response);
                }
            }
        };
    }
    
    @Value
    @ToString
    @SuppressWarnings("WeakerAccess")
    public static final class RESTObject {
        private final Route route;
        private final String[] routeParams;
        private final JSONObject payload;
    }
}
