package ls.ni.networkfilter.common;

import jakarta.validation.constraints.Null;
import lombok.Getter;
import ls.ni.networkfilter.common.cache.Cache;
import ls.ni.networkfilter.common.cache.CacheFactory;
import ls.ni.networkfilter.common.config.Config;
import ls.ni.networkfilter.common.config.ConfigManager;
import ls.ni.networkfilter.common.filter.*;
import org.apache.commons.net.util.SubnetUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkFilterCommon {

    private static NetworkFilterCommon instance;

    private final @NotNull Logger logger;
    @Getter
    private final @NotNull ConfigManager configManager;
    private final @NotNull Cache<String, FilterResult> filterCache;
    private final @NotNull FilterService filterService;

    public NetworkFilterCommon(@NotNull Logger logger, @NotNull ConfigManager configManager, @NotNull Cache<String, FilterResult> filterCache, @NotNull FilterService filterService) {
        this.logger = logger;
        this.configManager = configManager;
        this.filterCache = filterCache;
        this.filterService = filterService;
    }

    public static void init(@NotNull Logger logger, @NotNull File dataFolder) {
        if (instance != null) {
            throw new IllegalStateException("init() call but already initialized");
        }

        ConfigManager configManager = new ConfigManager(dataFolder);
        configManager.saveDefaultConfig();
        configManager.reloadConfig();

        Config config = configManager.getConfig();

        Cache cache = CacheFactory.create(config);
        FilterService service = FilterServiceFactory.create(config);

        logger.info("Using cache: " + cache.getName());
        logger.info("Using service: " + service.getName());

        instance = new NetworkFilterCommon(
                logger,
                configManager,
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

    public static @NotNull Config getConfig() {
        return getInstance().getConfigManager().getConfig();
    }

    public void debug(String message) {
        if (!getConfig().getDebug()) {
            return;
        }

        this.logger.info("[DEBUG] " + message);
    }

    public void debug(String pattern, Object... arguments) {
        if (!getConfig().getDebug()) {
            return;
        }

        this.debug(MessageFormat.format(pattern, arguments));
    }

    public @NotNull NetworkFilterResult check(@Nullable SocketAddress address) {
        if (!(address instanceof InetSocketAddress)) {
            throw new IllegalStateException("SocketAddress is not InetSocketAddress");
        }

        return check((InetSocketAddress) address);
    }

    public @NotNull NetworkFilterResult check(@Nullable InetSocketAddress address) {
        if (address == null || address.getAddress() == null) {
            throw new IllegalStateException("InetSocketAddress or InetAddress is null");
        }

        return check(address.getAddress().getHostAddress());
    }

    private @NotNull NetworkFilterResult check(@NotNull String ip) {
        long startTime = System.nanoTime();

        // cache
        Optional<FilterResult> cached = Optional.ofNullable(this.filterCache.getIfPresent(ip));
        if (cached.isPresent()) {
            this.debug("[{0}] Result is cached: {1}", ip, cached.get());

            return new NetworkFilterResult(
                    cached.get().block(),
                    cached.get().asn(),
                    cached.get().org(),
                    true,
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
            );
        }

        // ignore
        try {
            for (String network : this.configManager.getConfig().getIgnore().getNetworks()) {
                if (!network.contains("/") && network.equals(ip)) {
                    FilterResult filterResult = new FilterResult(false, null, null);

                    this.filterCache.put(ip, filterResult);

                    this.debug("[{0}] IP is ignored: {1}", ip, network);

                    return new NetworkFilterResult(
                            false,
                            -1,
                            "Ignored Network",
                            false,
                            TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
                    );
                } else if (network.contains("/")) {
                    SubnetUtils subnetUtils = new SubnetUtils(network);

                    if (subnetUtils.getInfo().isInRange(ip)) {
                        FilterResult filterResult = new FilterResult(false, null, null);

                        this.filterCache.put(ip, filterResult);

                        this.debug("[{0}] IP is in ignored range: {1}", ip, network);

                        return new NetworkFilterResult(
                                false,
                                -1,
                                "Ignored Network",
                                false,
                                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
                        );
                    }
                }
            }
        } catch (Throwable t) {
            this.logger.log(Level.SEVERE, "Error while checking inetAddress for ignored", t);
        }

        // check
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

        this.debug("[{0}] Requested: {1}", ip, filterResult);

        return new NetworkFilterResult(
                filterResult.block(),
                filterResult.asn(),
                filterResult.org(),
                false,
                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
        );
    }
}
