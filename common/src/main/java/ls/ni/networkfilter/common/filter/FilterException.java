package ls.ni.networkfilter.common.filter;

import kong.unirest.core.JsonNode;
import lombok.Getter;

@Getter
public class FilterException extends RuntimeException {

    private final int code;

    private final JsonNode body;

    public FilterException(int code, JsonNode body, String message) {
        super(message);
        this.code = code;
        this.body = body;
    }

    public FilterException(int code, JsonNode body, Throwable cause) {
        super(cause);
        this.code = code;
        this.body = body;
    }

    public FilterException(int code, JsonNode body, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.body = body;
    }
}
