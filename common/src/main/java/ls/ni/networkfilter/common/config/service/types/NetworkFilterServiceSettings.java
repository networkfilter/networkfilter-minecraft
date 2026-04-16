package ls.ni.networkfilter.common.config.service.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetworkFilterServiceSettings {

    @Nullable
    private String key;
}
