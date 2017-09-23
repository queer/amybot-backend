package chat.amy.queue;

import chat.amy.Backend;
import chat.amy.event.WrappedEvent;
import com.google.common.util.concurrent.AbstractScheduledService;
import lombok.ToString;
import lombok.Value;
import org.json.JSONObject;
import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author amy
 * @since 9/22/17.
 */
public class QueueProcessor extends AbstractScheduledService {
    private final Backend backend;
    private final String queue;
    private final RedissonClient redis;
    private final Logger logger;
    
    private static final String REST_QUEUE = "rest-requester";
    
    public QueueProcessor(final Backend backend, final String queue, final int idx) {
        this.backend = backend;
        this.queue = queue;
        logger = LoggerFactory.getLogger("Gateway " + queue + " Processor " + idx);
        
        final Config config = new Config();
        config.useSingleServer().setAddress(Optional.ofNullable(System.getenv("REDIS_HOST")).orElse("redis://redis:6379"))
                // Based on my bot heavily abusing redis as it is, high connection pool size is not a terrible idea.
                // NOTE: Current live implementation uses like 500 connections in the pool, so TEST TEST TEST
                // TODO: Determine better sizing
                .setConnectionPoolSize(128);
        redis = Redisson.create(config);
    }
    
    @Override
    protected void runOneIteration() throws Exception {
        logger.debug("Getting next event from " + queue + "...");
        final RBlockingQueue<WrappedEvent> blockingQueue = redis.getBlockingQueue(queue);
        final WrappedEvent event = blockingQueue.take();
        if(backend.getMessageProcessor().validate(event)) {
            backend.getMessageProcessor().process(event);
        } else {
            logger.debug("Discarding event: " + event);
        }
    }
    
    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, Long.parseLong(Optional.ofNullable(System.getenv("POLL_DELAY")).orElse("50")),
                TimeUnit.MILLISECONDS);
    }
    
    // TODO: This should send to the FakeJDA requester
    public void queue(RESTObject object) {
        final RBlockingQueue<RESTObject> blockingQueue = redis.getBlockingQueue(REST_QUEUE);
        logger.debug("Queueing new " + REST_QUEUE + " event: " + object);
        blockingQueue.add(object);
    }
    
    @Value
    @ToString
    public static final class RESTObject {
        private final String routeName;
        private final String[] routeParams;
        private final JSONObject payload;
    }
}
