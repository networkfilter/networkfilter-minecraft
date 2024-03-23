package ls.ni.networkfilter.common.config.consequence;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsequenceSettings {

    @Valid
    private KickConsequenceSettings kick;

    private List<String> commands;
}
