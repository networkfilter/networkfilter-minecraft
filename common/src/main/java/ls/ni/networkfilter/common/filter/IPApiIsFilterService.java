package ls.ni.networkfilter.common.filter;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class IPApiIsFilterService implements FilterService {

    @Nullable
    private final String apiKey;

    @NotNull
    private final Set<String> checkTypes;

    public IPApiIsFilterService(@Nullable String apiKey, @NotNull Set<String> checkTypes) {
        this.apiKey = apiKey;
        this.checkTypes = checkTypes;
    }

    @Override
    public @NotNull String getName() {
        return "ipapiis";
    }

    @Override
    public @NotNull FilterResult check(@NotNull String ip) {
        HttpResponse<JsonNode> response = Unirest.get("https://api.ipapi.is?q=" + ip + (this.apiKey != null ? "&api=" + this.apiKey : ""))
                .asJson();

        if (!response.isSuccess()) {
            throw new FilterException(response.getStatus(), response.getBody(), "Response is not successful");
        }

        if(response.getBody().getObject().getBoolean("is_bogon")) {
            return new FilterResult(
                    false,
                    -1,
                    "Internal Network"
            );
        }

        for (String checkType : this.checkTypes) {
            boolean result = response.getBody().getObject().getBoolean("is_" + checkType);

            if (result) {
                return new FilterResult(
                        true,
                        response.getBody().getObject().getJSONObject("asn").getInt("asn"),
                        response.getBody().getObject().getJSONObject("asn").getString("org")
                );
            }
        }

        return new FilterResult(
                false,
                response.getBody().getObject().getJSONObject("asn").getInt("asn"),
                response.getBody().getObject().getJSONObject("asn").getString("org")
        );
    }
}
