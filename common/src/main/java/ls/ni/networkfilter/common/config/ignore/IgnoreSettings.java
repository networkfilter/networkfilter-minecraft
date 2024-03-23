package ls.ni.networkfilter.common.config.ignore;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IgnoreSettings {

    private Set<String> networks;

    @NotBlank
    private String permission;
}
