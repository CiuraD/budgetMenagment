package pl.allegro.agh.budgetManagement.budget.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Instant;

@Component
public class TransactionLogger {

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> commands;

    @PostConstruct
    public void init() {
        try {
            String uri = String.format("redis://%s:%d", redisHost, redisPort);
            redisClient = RedisClient.create(uri);
            connection = redisClient.connect();
            commands = connection.sync();
        } catch (Exception e) {
            // fail safe: if Redis not available, keep commands null
            redisClient = null;
            connection = null;
            commands = null;
        }
    }

    public void log(String type, Object payload) {
        try {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("type", type);
            node.put("timestamp", Instant.now().toString());
            node.set("payload", objectMapper.valueToTree(payload));
            String json = objectMapper.writeValueAsString(node);

            if (commands == null) return;
            commands.lpush("transactions", json);
        } catch (Exception e) {
            // fail safe
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            if (connection != null) connection.close();
            if (redisClient != null) redisClient.shutdown();
        } catch (Exception ignored) {
        }
    }
}
