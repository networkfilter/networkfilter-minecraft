package ls.ni.networkfilter.common;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @param blocked
 * @param asn     only null if blocked
 * @param org     only null if blocked
 * @param cached
 * @param tookMs
 */
public record NetworkFilterResult(boolean blocked, @Nullable String asn, @Nullable String org, boolean cached,
                                  long tookMs) {
}
