/*
 * MIT License
 *
 * Copyright (c) 2019 Nils Müller
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ls.ni.networkfilter.bungee;

import lombok.Getter;
import ls.ni.networkfilter.bungee.listeners.PostLoginListener;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import ls.ni.networkfilter.common.config.Config;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NetworkFilterBungeePlugin extends Plugin {

    @Getter
    private Configuration config;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.reloadConfig();

        // ---

        NetworkFilterCommon.init(this.getLogger(), this.toConfig(getConfig()));

        // ---

        this.getProxy().getPluginManager().registerListener(this, new PostLoginListener(this));
    }

    private @NotNull Config toConfig(Configuration config) {
        String cache = config.getString("cache");
        String service = config.getString("service");
        Map<String, Map<String, Object>> caches = new HashMap<>();
        Map<String, Map<String, Object>> services = new HashMap<>();

        Configuration cacheConfigurationSection = config.getSection("caches");
        if (cacheConfigurationSection != null) {
            Collection<String> cacheTypes = cacheConfigurationSection.getKeys();

            for (String cacheType : cacheTypes) {
                Collection<String> cacheSettingKeys = cacheConfigurationSection.getSection(cacheType).getKeys();

                Map<String, Object> settings = new HashMap<>();
                for (String cacheSettingKey : cacheSettingKeys) {
                    settings.put(cacheSettingKey, cacheConfigurationSection.getSection(cacheType).get(cacheSettingKey));
                }
                caches.put(cacheType, settings);
            }
        }

        Configuration serviceConfigurationSection = config.getSection("services");
        if (serviceConfigurationSection != null) {
            Collection<String> serviceKeys = serviceConfigurationSection.getKeys();

            for (String serviceType : serviceKeys) {
                Collection<String> serviceSettingKeys = serviceConfigurationSection.getSection(serviceType).getKeys();

                Map<String, Object> settings = new HashMap<>();
                for (String serviceSettingKey : serviceSettingKeys) {
                    settings.put(serviceSettingKey, serviceConfigurationSection.getSection(serviceType).get(serviceSettingKey));
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

    private void reloadConfig() {
        try {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, new File(this.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDefaultConfig() {
        if (!this.getDataFolder().exists())
            this.getDataFolder().mkdir();

        File file = new File(this.getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = this.getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
