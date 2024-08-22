package ls.ni.networkfilter.common.config.cache;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CacheType {
    DISABLED("disabled"),
    LOCAL("local"),
    REDIS("redis"),
    ;

    private final String key;

    CacheType(String key) {
        this.key = key;
    }

    @JsonCreator
    public static CacheType fromString(String key) {
        if (key == null) {
            return null;
        }

        return CacheType.valueOf(key.toUpperCase());
    }

    @JsonValue
    public String getKey() {
        return key;
    }
}
