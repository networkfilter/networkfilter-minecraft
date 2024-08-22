package ls.ni.networkfilter.common.cache.types;

import ls.ni.networkfilter.common.cache.Cache;
import ls.ni.networkfilter.common.filter.FilterResult;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class NoopCache implements Cache {

    @Override
    public @NotNull String getName() {
        return "disabled";
    }

    @Override
    public FilterResult getIfPresent(@NotNull String key) {
        return null;
    }

    @Override
    public @NotNull FilterResult get(@NotNull String key, Function<String, ? extends FilterResult> mappingFunction) {
        return mappingFunction.apply(key);
    }

    @Override
    public void put(@NotNull String key, @NotNull FilterResult value) {
    }
}
