package ls.ni.networkfilter.common.cache;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface Cache<K, V> {

    @NotNull String getName();

    @Nullable  V getIfPresent(@NotNull K key);

    @NotNull V get(@NotNull K key, Function<? super K, ? extends @NotNull V> mappingFunction);

    void put(@NotNull K key, @NotNull V value);
}
