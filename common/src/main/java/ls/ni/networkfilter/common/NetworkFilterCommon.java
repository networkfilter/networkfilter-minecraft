package ls.ni.networkfilter.common;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import lombok.Getter;
import ls.ni.networkfilter.common.cache.Cache;
import ls.ni.networkfilter.common.cache.CacheFactory;
import ls.ni.networkfilter.common.config.Config;
import ls.ni.networkfilter.common.config.ConfigManager;
import ls.ni.networkfilter.common.filter.FilterException;
import ls.ni.networkfilter.common.filter.FilterResult;
import ls.ni.networkfilter.common.filter.FilterService;
import ls.ni.networkfilter.common.filter.FilterServiceFactory;
import ls.ni.networkfilter.common.util.PlaceholderUtil;
import org.apache.commons.net.util.SubnetUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;
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
                    ip,
                    true,
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
            );
        }

        // ignore
        try {
            for (String network : this.configManager.getConfig().getIgnore().getNetworks()) {
                if (!network.contains("/")) {
                    this.logger.warning(network + " is not in CIDR notation, assuming /32");
                    network = network + "/32";
                }

                SubnetUtils subnetUtils = new SubnetUtils(network);

                if (subnetUtils.getInfo().isInRange(ip)) {
                    FilterResult filterResult = new FilterResult(false, null, null);

                    this.filterCache.put(ip, filterResult);

                    this.debug("[{0}] IP is in ignored range: {1}", ip, network);

                    return new NetworkFilterResult(
                            false,
                            -1,
                            "Ignored Network",
                            ip,
                            false,
                            TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
                    );
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
                ip,
                false,
                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
        );
    }

    public void sendNotify(NetworkFilterResult result, String name, UUID uuid) {
        if (this.getConfigManager().getConfig().getNotify().getDiscord().getEnabled()) {
            String webhookUrl = this.getConfigManager().getConfig().getNotify().getDiscord().getWebhook();

            try (WebhookClient client = WebhookClient.withUrl(webhookUrl)) {
                String message = PlaceholderUtil.replace(this.getConfigManager().getConfig().getNotify().getDiscord().getMessage(),
                        result, name, uuid);

                WebhookEmbed embed = new WebhookEmbedBuilder()
                        .setColor(0xFF0000)
                        .setDescription(message)
                        .setThumbnailUrl("https://mc-api.io/render/" + uuid)
                        .addField(new WebhookEmbed.EmbedField(true, "ASN", String.valueOf(result.asn())))
                        .addField(new WebhookEmbed.EmbedField(true, "Organisation", String.valueOf(result.org())))
                        .addField(new WebhookEmbed.EmbedField(true, "Took", result.tookMs() + "ms"))
                        .build();

                client.send(embed);
            }
        }
    }
}
