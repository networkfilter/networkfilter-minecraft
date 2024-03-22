package ls.ni.networkfilter.common.config.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ls.ni.networkfilter.common.config.service.types.IpApiIsServiceSettings;
import ls.ni.networkfilter.common.config.service.types.NetworkFilterServiceSettings;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceSettings {

    @Valid
    @NotNull
    @JsonProperty("networkfilter")
    private NetworkFilterServiceSettings networkFilter;

    @Valid
    @NotNull
    @JsonProperty("ipapiis")
    private IpApiIsServiceSettings ipApiIs;
}
