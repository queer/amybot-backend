package chat.amy.queue;

import chat.amy.Backend;
import chat.amy.discord.FakeJDA;
import chat.amy.event.WrappedEvent;
import lombok.ToString;
import lombok.Value;
import net.dv8tion.jda.core.requests.Requester;
import net.dv8tion.jda.core.requests.Route;
import org.json.JSONObject;
import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author amy
 * @since 9/22/17.
 */
public class QueueProcessor {
    private static final String REST_QUEUE = "rest-requester";
    private final Backend backend;
    private final String queue;
    private final RedissonClient redis;
    private final Logger logger;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final FakeJDA jda;
    
    public QueueProcessor(final Backend backend, final String queue, final int idx) {
        this.backend = backend;
        this.queue = queue;
        logger = LoggerFactory.getLogger("Gateway " + queue + " Processor " + idx);
        jda = new FakeJDA(System.getenv("BOT_TOKEN"));
        
        final Config config = new Config();
        config.useSingleServer().setAddress(Optional.ofNullable(System.getenv("REDIS_HOST")).orElse("redis://redis:6379"))
                .setPassword(System.getenv("REDIS_PASS"))
                // Based on my bot heavily abusing redis as it is, high connection pool size is not a terrible idea.
                // NOTE: Current live implementation uses like 500 connections in the pool, so TEST TEST TEST
                // TODO: Determine better sizing
                .setConnectionPoolSize(128);
        redis = Redisson.create(config);
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
                try {
                    logger.debug("Getting next event from " + queue + "...");
                    final RBlockingQueue<WrappedEvent> blockingQueue = redis.getBlockingQueue(queue);
                    final WrappedEvent event = blockingQueue.take();
                    if(backend.getMessageProcessor().validate(event)) {
                        backend.getMessageProcessor().process(event);
                    } else {
                        logger.debug("Discarding event: " + event);
                    }
                } catch(final InterruptedException e) {
                    logger.warn("Caught exception polling the event queue: {}", e);
                }
            }
        }
    }
}
