package ls.ni.networkfilter.common.config.service.types;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpApiIsServiceSettings {

    @NotNull
    private String key;

    @NotNull
    private Map<@NotNull String, @NotNull Boolean> block;
}
