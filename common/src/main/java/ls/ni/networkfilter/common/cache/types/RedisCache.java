package ls.ni.networkfilter.common.cache.types;

import com.fasterxml.jackson.databind.ObjectMapper;
import ls.ni.networkfilter.common.cache.Cache;
import ls.ni.networkfilter.common.filter.FilterResult;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.function.Function;

public class RedisCache implements Cache {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JedisPool jedisPool;
    private final Duration expireAfterWrite;

    public RedisCache(@NotNull String host, int port, @NotNull String password, @NotNull Duration expireAfterWrite) {
        this.jedisPool = new JedisPool(new JedisPoolConfig(), host, port, 2000, password.isEmpty() ? null : password);

        this.expireAfterWrite = expireAfterWrite;

        // Close the JedisPool when the JVM is shutting down
        Runtime.getRuntime().addShutdownHook(new Thread(this.jedisPool::close));
    }

    @Override
    public @NotNull String getName() {
        return "redis";
    }

    @Override
    public @Nullable FilterResult getIfPresent(@NotNull String key) {
        String cacheKey = this.cacheKey(key);

        try (Jedis resource = this.jedisPool.getResource()) {
            if (!resource.exists(cacheKey)) {
                return null;
            }

            return this.objectMapper.readValue(resource.get(cacheKey), FilterResult.class);
        } catch (Throwable cause) {
            throw new RuntimeException("Failed to get value from Redis", cause);
        }
    }

    @Override
    public @NotNull FilterResult get(@NotNull String key, Function<String, ? extends @NotNull FilterResult> mappingFunction) {
        FilterResult value = this.getIfPresent(key);

        // If the value is not present, compute it and put it to the cache
        if (value == null) {
            value = mappingFunction.apply(key);
            this.put(key, value);
        }

        return value;
    }

    @Override
    public void put(@NotNull String key, @NotNull FilterResult value) {
        String cacheKey = this.cacheKey(key);

        try (Jedis resource = this.jedisPool.getResource()) {
            String mappedValue = this.objectMapper.writeValueAsString(value);

            resource.setex(cacheKey, (int) this.expireAfterWrite.getSeconds(), mappedValue);
        } catch (Throwable cause) {
            throw new RuntimeException("Failed to put value to Redis", cause);
        }
    }

    private String cacheKey(String key) {
        return "networkfilter:" + key;
    }

}
