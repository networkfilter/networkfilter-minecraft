package ls.ni.networkfilter.common.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ls.ni.networkfilter.common.config.cache.CacheSettings;
import ls.ni.networkfilter.common.config.cache.CacheType;
import ls.ni.networkfilter.common.config.consequence.ConsequenceSettings;
import ls.ni.networkfilter.common.config.ignore.IgnoreSettings;
import ls.ni.networkfilter.common.config.notify.NotifySettings;
import ls.ni.networkfilter.common.config.service.ServiceSettings;
import ls.ni.networkfilter.common.config.service.ServiceType;

import java.util.List;

@Valid
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Config {

    @Valid
    @NotNull
    private Boolean debug;

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

    @Valid
    @NotNull
    private IgnoreSettings ignore;

    @Valid
    @NotNull
    private List<Integer> asnWhitelist;

    @Valid
    @NotNull
    private ConsequenceSettings consequences;

    @Valid
    @NotNull
    private NotifySettings notify;
}
