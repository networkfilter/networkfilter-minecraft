package ls.ni.networkfilter.common.cache.types;

import com.github.benmanes.caffeine.cache.Caffeine;
import ls.ni.networkfilter.common.cache.Cache;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.function.Function;

public class CaffeineCache<K, V> implements Cache<K, V> {

    private final com.github.benmanes.caffeine.cache.Cache<K, V> cache;

    public CaffeineCache(@NotNull Long maximumSize, @NotNull Duration expireAfterWrite) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expireAfterWrite)
                .build();
    }

    @Override
    public @NotNull String getName() {
        return "local";
    }

    @Override
    public @Nullable V getIfPresent(@NotNull K key) {
        return this.cache.getIfPresent(key);
    }

    @Override
    public @NotNull V get(@NotNull K key, Function<? super K, ? extends @NotNull V> mappingFunction) {
        return this.cache.get(key, mappingFunction);
    }

    @Override
    public void put(@NotNull K key, @NotNull V value) {
        this.cache.put(key, value);
    }
}
