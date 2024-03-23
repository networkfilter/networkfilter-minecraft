package ls.ni.networkfilter.common.config.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ServiceType {
    NETWORKFILTER("networkfilter"),
    IPAPIIS("ipapiis"),
    ;

    private final String key;

    ServiceType(String key) {
        this.key = key;
    }

    @JsonCreator
    public static ServiceType fromString(String key) {
        if (key == null) {
            return null;
        }

        return ServiceType.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String getKey() {
        return key;
    }
}
