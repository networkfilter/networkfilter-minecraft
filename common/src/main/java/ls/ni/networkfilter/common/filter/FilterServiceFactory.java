package ls.ni.networkfilter.common.filter;

import ls.ni.networkfilter.common.config.Config;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterServiceFactory {

    public static FilterService create(@NotNull Config config) {
        if (config.service() == null) {
            throw new IllegalStateException("Service 'service' is not set!");
        }

        return switch (config.service()) {
            case "networkfilter" ->
                    new NetworkFilterFilterService((String) config.services().get(config.service()).get("key"));
            case "ipapiis" -> {
                Set<String> checkTypes = config.services().get(config.service()).entrySet().stream()
                        .filter(stringObjectEntry -> stringObjectEntry.getKey().startsWith("block"))
                        .map(stringObjectEntry -> Map.entry(stringObjectEntry.getKey(), (Boolean) stringObjectEntry.getValue()))
                        .filter(Map.Entry::getValue)
                        .map(Map.Entry::getKey)
                        .map(s -> s.replaceFirst("block", "").toLowerCase(Locale.ROOT))
                        .collect(Collectors.toSet());

                yield new IPApiIsFilterService((String) config.services().get(config.service()).get("key"), checkTypes);
            }
            default -> throw new IllegalStateException("Service '" + config.service() + "' is not supported!");
        };
    }
}
