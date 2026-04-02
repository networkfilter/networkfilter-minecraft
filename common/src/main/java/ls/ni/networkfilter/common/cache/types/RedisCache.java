package ls.ni.networkfilter.common.cache.types;

import com.fasterxml.jackson.databind.ObjectMapper;
import ls.ni.networkfilter.common.cache.Cache;
import ls.ni.networkfilter.common.filter.FilterResult;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.*;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RedisCache implements Cache {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UnifiedJedis unifiedJedis;
    private final Duration expireAfterWrite;

    private static final int SENTINEL_DEFAULT_PORT = 26379;

    public RedisCache(@NotNull String uri, @NotNull Duration expireAfterWrite) {
        this.unifiedJedis = new JedisPooled(uri);

        this.expireAfterWrite = expireAfterWrite;

        // Close the JedisPool when the JVM is shutting down
        Runtime.getRuntime().addShutdownHook(new Thread(this.unifiedJedis::close));
    }

    public RedisCache(String masterName, List<String> sentinelAddresses, String username, String password,
                     boolean ssl, String sentinelUsername, String sentinelPassword, Duration expireAfterWrite){

        this.expireAfterWrite = expireAfterWrite;

        Set<HostAndPort> sentinels = sentinelAddresses.stream()
                .map(addr -> parseAddress(addr))
                .collect(Collectors.toSet());

        this.unifiedJedis = new JedisSentineled(masterName, jedisConfig(username, password, ssl),
                sentinels, jedisConfig(sentinelUsername, sentinelPassword, ssl));
    }

    private static HostAndPort parseAddress(String address) {
        return parseAddress(address, Protocol.DEFAULT_PORT);
    }

    private static HostAndPort parseAddress(String address, int defaultPort) {
        return new HostAndPort(address, defaultPort);
    }

    private static JedisClientConfig jedisConfig(String username, String password, boolean ssl) {
        return DefaultJedisClientConfig.builder()
                .user(username)
                .password(password)
                .ssl(ssl)
                .timeoutMillis(Protocol.DEFAULT_TIMEOUT)
                .build();
    }

    @Override
    public @NotNull String getName() {
        return "redis";
    }

    @Override
    public @Nullable FilterResult getIfPresent(@NotNull String key) {
        String cacheKey = this.cacheKey(key);

        try {
            if (!this.unifiedJedis.exists(cacheKey)) {
                return null;
            }

            return this.objectMapper.readValue(this.unifiedJedis.get(cacheKey), FilterResult.class);
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

        try {
            String mappedValue = this.objectMapper.writeValueAsString(value);

            this.unifiedJedis.setex(cacheKey, (int) this.expireAfterWrite.getSeconds(), mappedValue);

        } catch (Throwable cause) {
            throw new RuntimeException("Failed to put value to Redis", cause);
        }
    }

    private String cacheKey(String key) {
        return "networkfilter:" + key;
    }

}
