package ls.ni.networkfilter.common;

import org.jspecify.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * @param blocked
 * @param asn     only null if blocked
 * @param org     only null if blocked
 * @param cached
 * @param tookMs
 */
public record NetworkFilterResult(boolean blocked, @Nullable Integer asn, @Nullable String org, @NotNull String ip, boolean cached,
                                  long tookMs) {
}
