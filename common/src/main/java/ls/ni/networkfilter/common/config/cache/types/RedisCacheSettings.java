package ls.ni.networkfilter.common.config.cache.types;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisCacheSettings {

    @NotNull
    private String host;

    @NotNull
    @Positive
    private Integer port;

    @NotNull
    private String password;

    @NotNull
    @Positive
    private Long cacheTimeMinutes;
}
