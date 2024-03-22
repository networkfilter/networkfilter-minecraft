package ls.ni.networkfilter.common.filter;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONObject;
import org.jetbrains.annotations.NotNull;

public class NetworkFilterFilterService implements FilterService {

    @NotNull
    private final String apiKey;

    public NetworkFilterFilterService(@NotNull String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public @NotNull String getName() {
        return "networkfilter";
    }

    @Override
    public @NotNull FilterResult check(@NotNull String ip) {
        HttpResponse<JsonNode> response = Unirest.post("https://nf.ni.ls/api/check")
                .header("X-API-KEY", this.apiKey)
                .header("Content-Type", "application/x-www-form-urlencoded")
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

        return new FilterResult(
                data.getBoolean("blocked"),
                data.getInt("asn"),
                data.getString("org")
        );
    }
}
