package ls.ni.networkfilter.common.filter;

import ls.ni.networkfilter.common.config.Config;
import ls.ni.networkfilter.common.filter.types.IPApiIsFilterService;
import ls.ni.networkfilter.common.filter.types.NetworkFilterFilterService;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterServiceFactory {

    public static FilterService create(@NotNull Config config) {
        return switch (config.getService()) {
            case NETWORKFILTER -> new NetworkFilterFilterService(config.getServices().getNetworkFilter().getKey());
            case IPAPIIS -> {
                Set<String> checkTypes = config.getServices().getIpApiIs().getBlock().entrySet().stream()
                        .filter(Map.Entry::getValue)
                        .map(Map.Entry::getKey)
                        .map(s -> s.toLowerCase(Locale.ROOT))
                        .collect(Collectors.toSet());

                yield new IPApiIsFilterService(config.getServices().getIpApiIs().getKey(), checkTypes);
            }
            default -> throw new IllegalStateException("Service '" + config.getService() + "' is not supported!");
        };
    }
}
