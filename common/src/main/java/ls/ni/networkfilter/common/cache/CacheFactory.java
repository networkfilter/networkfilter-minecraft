package ls.ni.networkfilter.common.cache;

import ls.ni.networkfilter.common.cache.types.CaffeineCache;
import ls.ni.networkfilter.common.cache.types.NoopCache;
import ls.ni.networkfilter.common.cache.types.RedisCache;
import ls.ni.networkfilter.common.config.Config;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class CacheFactory {

    public static Cache create(@NotNull Config config) {
        return switch (config.getCache()) {
            case DISABLED -> new NoopCache();
            case LOCAL -> {
                yield new CaffeineCache(
                        config.getCaches().getLocal().getMaximumSize(),
                        Duration.ofMinutes(config.getCaches().getLocal().getCacheTimeMinutes())
                );
            }
            case REDIS -> {
                yield new RedisCache(
                        config.getCaches().getRedis().getUri(),
                        Duration.ofMinutes(config.getCaches().getRedis().getCacheTimeMinutes())
                );
            }
            default -> throw new IllegalStateException("Cache '" + config.getCache() + "' is not supported!");
        };
    }
}
