package ls.ni.networkfilter.common.cache;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class NoopCache<K, V> implements Cache<K, V> {

    @Override
    public @NotNull String getName() {
        return "disabled";
    }

    @Override
    public V getIfPresent(@NotNull K key) {
        return null;
    }

    @Override
    public @NotNull V get(@NotNull K key, Function<? super K, ? extends V> mappingFunction) {
        return mappingFunction.apply(key);
    }

    @Override
    public void put(@NotNull K key, @NotNull V value) {
    }
}
