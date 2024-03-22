package ls.ni.networkfilter.common.filter;

import org.jetbrains.annotations.NotNull;

public interface FilterService {

    @NotNull String getName();

    @NotNull FilterResult check(@NotNull String ip);
}
