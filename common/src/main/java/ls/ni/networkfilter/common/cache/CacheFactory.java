package ls.ni.networkfilter.common.cache;

import ls.ni.networkfilter.common.config.Config;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class CacheFactory {

    public static Cache<?, ?> create(@NotNull Config config) {
        if (config.cache() == null) {
            throw new IllegalStateException("Cache 'cache' is not set!");
        }

        return switch (config.cache()) {
            case "disabled" -> new NoopCache<>();
            case "local" -> new CaffeineCache<>(
                    (Long) config.caches().get(config.cache()).get("maximumSize"),
                    Duration.ofMinutes((Long) config.caches().get(config.cache()).get("cacheTimeMinutes"))
            );
            default -> throw new IllegalStateException("Cache '" + config.cache() + "' is not supported!");
        };
    }
}
