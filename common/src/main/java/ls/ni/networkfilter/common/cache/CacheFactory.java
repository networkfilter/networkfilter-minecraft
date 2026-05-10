package ls.ni.networkfilter.common.cache;

import ls.ni.networkfilter.common.cache.types.CaffeineCache;
import ls.ni.networkfilter.common.cache.types.NoopCache;
import ls.ni.networkfilter.common.cache.types.RedisCache;
import ls.ni.networkfilter.common.config.Config;
import ls.ni.networkfilter.common.config.cache.types.ApiCacheSettings;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class CacheFactory {

    public static Cache create(@NotNull Config config) {
        ApiCacheSettings apiCache = config.getApiCache();
        if (apiCache != null && Boolean.TRUE.equals(apiCache.getEnabled())) {
            long maximumSize = apiCache.getMaximumSize() != null ? apiCache.getMaximumSize() : 1000L;
            long cacheTimeMinutes = apiCache.getCacheTimeMinutes() != null ? apiCache.getCacheTimeMinutes() : 60L;

            return new CaffeineCache(
                    maximumSize,
                    Duration.ofMinutes(cacheTimeMinutes)
            );
        }

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
