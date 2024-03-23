package ls.ni.networkfilter.common.filter.types;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONObject;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import ls.ni.networkfilter.common.filter.FilterException;
import ls.ni.networkfilter.common.filter.FilterResult;
import ls.ni.networkfilter.common.filter.FilterService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
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

        JSONObject asn = response.getBody().getObject().getJSONObject("asn");

        for (String checkType : this.checkTypes) {
            boolean result = response.getBody().getObject().getBoolean("is_" + checkType);

            if (result) {
                NetworkFilterCommon.getInstance().debug("[{0}] {1} blocked by check {2}", ip, this.getClass().getSimpleName(), checkType);
                return new FilterResult(
                        true,
                        Optional.ofNullable(asn).map(jsonObject -> jsonObject.getInt("asn")).orElse(-1),
                        Optional.ofNullable(asn).map(jsonObject -> jsonObject.getString("org")).orElse("Unknown")
                );
            }
        }

        return new FilterResult(
                false,
                Optional.ofNullable(asn).map(jsonObject -> jsonObject.getInt("asn")).orElse(-1),
                Optional.ofNullable(asn).map(jsonObject -> jsonObject.getString("org")).orElse("Unknown")
        );
    }
}
