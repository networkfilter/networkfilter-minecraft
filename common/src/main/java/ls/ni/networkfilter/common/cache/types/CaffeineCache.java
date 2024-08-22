package ls.ni.networkfilter.common.cache.types;

import com.github.benmanes.caffeine.cache.Caffeine;
import ls.ni.networkfilter.common.cache.Cache;
import ls.ni.networkfilter.common.filter.FilterResult;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.function.Function;

public class CaffeineCache implements Cache {

    private final com.github.benmanes.caffeine.cache.Cache<String, FilterResult> cache;

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
    public @Nullable FilterResult getIfPresent(@NotNull String key) {
        return this.cache.getIfPresent(key);
    }

    @Override
    public @NotNull FilterResult get(@NotNull String key, Function<String, ? extends @NotNull FilterResult> mappingFunction) {
        return this.cache.get(key, mappingFunction);
    }

    @Override
    public void put(@NotNull String key, @NotNull FilterResult value) {
        this.cache.put(key, value);
    }
}
