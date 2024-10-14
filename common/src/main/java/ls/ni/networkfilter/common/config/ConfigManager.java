package ls.ni.networkfilter.common.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigManager {

    private final File dataFolder;
    private final File configFile;
    private final YAMLMapper mapper;

    private Config config;

    public ConfigManager(@NotNull File dataFolder) {
        this.dataFolder = dataFolder;
        this.configFile = new File(this.dataFolder, "config.yml");

        this.mapper = YAMLMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();
    }

    public void reloadConfig() {
        try {
            this.config = this.mapper.readValue(this.configFile, Config.class);
        } catch (IOException e) {
            throw new RuntimeException("Could not load config " + this.configFile, e);
        }
    }

    public Config getConfig() {
        if (this.config == null) {
            this.reloadConfig();
        }

        return this.config;
    }

    public void saveDefaultConfig() {
        if (this.configFile.exists()) {
            return;
        }

        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdir();
        }

        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config.yml")) {
            if (inputStream == null)
                throw new RuntimeException("config.yml not found");

            Files.copy(inputStream, this.configFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Could not copy default file " + this.configFile, e);
        }
    }
}
