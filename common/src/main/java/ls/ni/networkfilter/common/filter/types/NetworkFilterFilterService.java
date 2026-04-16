package ls.ni.networkfilter.common.filter.types;

import kong.unirest.core.HttpRequestWithBody;
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

import java.util.List;
import java.util.Optional;

public class NetworkFilterFilterService implements FilterService {

    @Nullable
    private final String apiKey;

    public NetworkFilterFilterService(@Nullable String apiKey) {
        this.apiKey = apiKey != null && !apiKey.isBlank() ? apiKey : null;
    }

    @Override
    public @NotNull String getName() {
        return "networkfilter";
    }

    @Override
    public @NotNull FilterResult check(@NotNull String ip) {
        HttpRequestWithBody request = Unirest.post("https://nf.ni.ls/api/check")
                .header("Content-Type", "application/x-www-form-urlencoded");

        if (this.apiKey != null) {
            request.header("X-API-KEY", this.apiKey);
        }

        HttpResponse<JsonNode> response = request
                .field("ip", ip)
                .asJson();

        if (!response.isSuccess()) {
            throw new FilterException(response.getStatus(), response.getBody(), "Response is not successful");
        }

        JSONObject body = response.getBody().getObject();

        if (!body.optBoolean("success", false)) {
            throw new FilterException(response.getStatus(), response.getBody(), "API body response is not successful");
        }

        JSONObject data = body.getJSONObject("data");

        //whitelist
        List<Integer> asnWhitelist = NetworkFilterCommon.getConfig().getAsnWhitelist();
        int asn = data.getInt("asn");
        if (asnWhitelist.contains(asn)) {
            return new FilterResult(
                    false,
                    asn,
                    data.getString("org")
            );
        }

        return new FilterResult(
                data.getBoolean("blocked"),
                asn,
                data.getString("org")
        );
    }
}
