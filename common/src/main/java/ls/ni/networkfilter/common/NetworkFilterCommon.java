package ls.ni.networkfilter.common;

import ls.ni.networkfilter.common.cache.Cache;
import ls.ni.networkfilter.common.cache.CacheFactory;
import ls.ni.networkfilter.common.cache.NoopCache;
import ls.ni.networkfilter.common.config.Config;
import ls.ni.networkfilter.common.filter.*;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkFilterCommon {

    private final @NotNull Logger logger;
    private final @NotNull Cache<String, FilterResult> filterCache;
    private final @NotNull FilterService filterService;

    private static NetworkFilterCommon instance;

    public NetworkFilterCommon(@NotNull Logger logger, @NotNull Cache<String, FilterResult> filterCache, @NotNull FilterService filterService) {
        this.logger = logger;
        this.filterCache = filterCache;
        this.filterService = filterService;
    }

    public static void init(@NotNull Logger logger, @NotNull Config config) {
        if (instance != null) {
            throw new IllegalStateException("init() call but already initialized");
        }

        if (config.cache() == null || config.cache().isBlank() || !config.caches().containsKey(config.cache())) {
            throw new IllegalStateException("Config value 'cache' not set, empty or does not exists in 'caches'");
        }

        if (config.service() == null || config.service().isBlank() || !config.services().containsKey(config.service())) {
            throw new IllegalStateException("Config value 'service' not set, empty or does not exists in 'services'");
        }

        Cache cache = CacheFactory.create(config);
        FilterService service = FilterServiceFactory.create(config);

        logger.info("Using cache: " + cache.getName());
        logger.info("Using service: " + service.getName());

        instance = new NetworkFilterCommon(
                logger,
                cache,
                service
        );
    }

    public static @NotNull NetworkFilterCommon getInstance() {
        if (instance == null) {
            throw new IllegalStateException("getInstance() call before init()");
        }

        return instance;
    }

    public NetworkFilterResult check(@NotNull String ip) {
        long startTime = System.nanoTime();

        Optional<FilterResult> cached = Optional.ofNullable(this.filterCache.getIfPresent(ip));

        if (cached.isPresent()) {
            return new NetworkFilterResult(
                    cached.get().block(),
                    cached.get().asn(),
                    cached.get().org(),
                    true,
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
            );
        }

        FilterResult filterResult;
        try {
            filterResult = this.filterService.check(ip);
        } catch (FilterException e) {
            this.logger.log(Level.SEVERE, "Could not check ip " + ip + " (status: " + e.getCode() + ", body: " + e.getBody().toString() + ")", e);

            // TODO: make configurable (something like "blockOnFilterServiceError") - should apply on rate limit?
            filterResult = new FilterResult(false, null, null);
        } catch (Throwable t) {
            this.logger.log(Level.SEVERE, "Could not check ip " + ip, t);

            // TODO: make configurable (something like "blockOnUnexpectedError")
            filterResult = new FilterResult(false, null, null);
        }


        this.filterCache.put(ip, filterResult);

        return new NetworkFilterResult(
                filterResult.block(),
                filterResult.asn(),
                filterResult.org(),
                false,
                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
        );
    }
}
