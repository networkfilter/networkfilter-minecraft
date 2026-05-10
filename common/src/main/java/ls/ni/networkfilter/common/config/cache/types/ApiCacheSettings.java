package ls.ni.networkfilter.common.config.cache.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiCacheSettings {

    private Boolean enabled;

    private Long maximumSize;

    private Long cacheTimeMinutes;
}
