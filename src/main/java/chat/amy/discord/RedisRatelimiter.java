package chat.amy.discord;

import net.dv8tion.jda.core.ShardedRateLimiter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author amy
 * @since 10/3/17.
 */
public class RedisRatelimiter extends ShardedRateLimiter {
    private static final String RATELIMIT_KEY = "sharded-rest-ratelimiter";
    private final JedisPool pool;
    
    public RedisRatelimiter(final JedisPool pool) {
        this.pool = pool;
        try(final Jedis jedis = pool.getResource()) {
            jedis.set(RATELIMIT_KEY, Long.toString(Long.MIN_VALUE));
        }
    }
    
    public long getGlobalRatelimit() {
        try(final Jedis jedis = pool.getResource()) {
            return Long.parseLong(jedis.get(RATELIMIT_KEY));
        }
    }
    
    public void setGlobalRatelimit(final long value) {
        try(final Jedis jedis = pool.getResource()) {
            jedis.set(RATELIMIT_KEY, Long.toString(value));
        }
    }
}
