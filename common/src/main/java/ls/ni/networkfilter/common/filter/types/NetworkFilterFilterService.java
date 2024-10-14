package ls.ni.networkfilter.common.filter.types;

import jakarta.validation.constraints.NotBlank;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import kong.unirest.core.json.JSONObject;
import ls.ni.networkfilter.common.NetworkFilterCommon;
import ls.ni.networkfilter.common.filter.FilterException;
import ls.ni.networkfilter.common.filter.FilterResult;
import ls.ni.networkfilter.common.filter.FilterService;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class NetworkFilterFilterService implements FilterService {

    @NotNull
    private final String apiKey;

    public NetworkFilterFilterService(@NotNull @NotBlank String apiKey) {
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
