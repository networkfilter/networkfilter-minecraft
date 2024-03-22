package ls.ni.networkfilter.common.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ls.ni.networkfilter.common.config.cache.CacheSettings;
import ls.ni.networkfilter.common.config.cache.CacheType;
import ls.ni.networkfilter.common.config.service.ServiceSettings;
import ls.ni.networkfilter.common.config.service.ServiceType;

@Valid
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Config {

    @Valid
    @NotNull
    private CacheType cache;

    @Valid
    @NotNull
    private ServiceType service;

    @Valid
    @NotNull
    private CacheSettings caches;

    @Valid
    @NotNull
    private ServiceSettings services;
}
