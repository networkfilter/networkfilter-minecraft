package ls.ni.networkfilter.common.util;

import ls.ni.networkfilter.common.NetworkFilterResult;

import java.util.Optional;
import java.util.UUID;

public class PlaceholderUtil {

    public static String replace(String message, NetworkFilterResult result, String name, UUID uuid) {
        message = message.replace("%asn%", Optional.ofNullable(result.asn()).map(String::valueOf).orElse("-1"));
        message = message.replace("%org%", Optional.ofNullable(result.org()).orElse("Unknown"));
        message = message.replace("%ip%", result.ip());
        message = message.replace("%name%", name);
        message = message.replace("%uuid%", uuid.toString());

        return message;
    }
}
