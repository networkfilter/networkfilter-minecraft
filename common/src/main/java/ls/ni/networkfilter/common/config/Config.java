package ls.ni.networkfilter.common.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record Config(@Nullable String cache, @Nullable String service, @NotNull Map<String, Map<String, Object>> caches,
                     @NotNull Map<String, Map<String, Object>> services) {
}
