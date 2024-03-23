package ls.ni.networkfilter.common.config.consequence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KickConsequenceSettings {

    private Boolean enabled;

    private String message;
}
