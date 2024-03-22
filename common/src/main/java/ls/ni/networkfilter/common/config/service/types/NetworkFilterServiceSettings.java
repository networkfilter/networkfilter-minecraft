package ls.ni.networkfilter.common.config.service.types;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetworkFilterServiceSettings {

    @NotNull
    private String key;
}
