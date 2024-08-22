package ls.ni.networkfilter.common.config.cache;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ls.ni.networkfilter.common.config.cache.types.DisabledCacheSettings;
import ls.ni.networkfilter.common.config.cache.types.LocalCacheSettings;
import ls.ni.networkfilter.common.config.cache.types.RedisCacheSettings;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CacheSettings {

    @Valid
    @NotNull
    private DisabledCacheSettings disabled;

    @Valid
    @NotNull
    private LocalCacheSettings local;

    @Valid
    @NotNull
    private RedisCacheSettings redis;

}
