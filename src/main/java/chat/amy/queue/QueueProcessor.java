package chat.amy.queue;

import chat.amy.Backend;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author amy
 * @since 9/22/17.
 */
public class QueueProcessor {
    private final Backend backend;
    private final String queue;
    private final JedisPool redis;
    private final Logger logger;
    private final ObjectMapper mapper = new ObjectMapper();
    
    public QueueProcessor(final Backend backend, final String queue, final int idx) {
        this.backend = backend;
        this.queue = queue;
        logger = LoggerFactory.getLogger("Backend " + queue + " Processor " + idx);
        
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(1024);
        jedisPoolConfig.setMaxTotal(1024);
        jedisPoolConfig.setMaxWaitMillis(500);
        redis = new JedisPool(jedisPoolConfig, Optional.ofNullable(System.getenv("REDIS_HOST")).orElse("redis://redis:6379"));
    }
    
    private void cache(final Consumer<Jedis> op) {
        try(Jedis jedis = redis.getResource()) {
            jedis.auth(System.getenv("REDIS_PASS"));
            op.accept(jedis);
        }
    }
    
    /**
     * Start the intake queue thread
     */
    public void startPolling() {
        final Thread intakeThread = new Thread(new IntakeQueueThread());
        intakeThread.setName("amybot backend intake thread");
        intakeThread.start();
    }
    
    private final class IntakeQueueThread implements Runnable {
        @Override
        public void run() {
            while(true) {
                // TODO: Implement Q_Q
            }
        }
    }
}
