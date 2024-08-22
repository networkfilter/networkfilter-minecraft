package ls.ni.networkfilter.common.cache;

import ls.ni.networkfilter.common.filter.FilterResult;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface Cache {

    /**
     * Get the name of the cache
     *
     * @return the name of the cache
     */
    @NotNull String getName();

    /**
     * Get the value associated with the key
     *
     * @param key the key
     * @return the value associated with the key, or null if the key is not present
     */
    @Nullable
    FilterResult getIfPresent(@NotNull String key);

    /**
     * Get the value associated with the key, or compute the value if the key is not present
     *
     * @param key the key
     * @param mappingFunction the function to compute the value if the key is not present
     * @return the value associated with the key
     */
    @NotNull FilterResult get(@NotNull String key, Function<String, ? extends @NotNull FilterResult> mappingFunction);

    /**
     * Put the value associated with the key
     *
     * @param key the key
     * @param value the value
     */
    void put(@NotNull String key, @NotNull FilterResult value);
}
