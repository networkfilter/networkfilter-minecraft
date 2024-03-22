package ls.ni.networkfilter.common.filter;

import org.jetbrains.annotations.Nullable;

/**
 * @param block if the ip should be blocked
 * @param asn   could be null if not block
 * @param org   could be null if not block
 */
public record FilterResult(boolean block, @Nullable String asn, @Nullable String org) {

}
