package ls.ni.networkfilter.bukkit;

import ls.ni.networkfilter.bukkit.listeners.PlayerJoinListener;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import ls.ni.networkfilter.common.config.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NetworkFilterBukkitPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.reloadConfig();
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        // ---

        NetworkFilterCommon.init(this.getLogger(), this.toConfig(getConfig()));

        // ---

        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    private @NotNull Config toConfig(FileConfiguration config) {
        String cache = config.getString("cache");
        String service = config.getString("service");
        Map<String, Map<String, Object>> caches = new HashMap<>();
        Map<String, Map<String, Object>> services = new HashMap<>();

        ConfigurationSection cacheConfigurationSection = config.getConfigurationSection("caches");
        if (cacheConfigurationSection != null) {
            Collection<String> cacheTypes = cacheConfigurationSection.getKeys(false);

            for (String cacheType : cacheTypes) {
                Collection<String> cacheSettingKeys = cacheConfigurationSection.getConfigurationSection(cacheType).getKeys(false);

                Map<String, Object> settings = new HashMap<>();
                for (String cacheSettingKey : cacheSettingKeys) {
                    settings.put(cacheSettingKey, cacheConfigurationSection.getConfigurationSection(cacheType).get(cacheSettingKey));
                }
                caches.put(cacheType, settings);
            }
        }

        ConfigurationSection serviceConfigurationSection = config.getConfigurationSection("services");
        if (serviceConfigurationSection != null) {
            Collection<String> serviceKeys = serviceConfigurationSection.getKeys(false);

            for (String serviceType : serviceKeys) {
                Collection<String> serviceSettingKeys = serviceConfigurationSection.getConfigurationSection(serviceType).getKeys(false);

                Map<String, Object> settings = new HashMap<>();
                for (String serviceSettingKey : serviceSettingKeys) {
                    settings.put(serviceSettingKey, serviceConfigurationSection.getConfigurationSection(serviceType).get(serviceSettingKey));
                }
                services.put(serviceType, settings);
            }
        }

        return new Config(
                cache,
                service,
                caches,
                services
        );
    }
}
