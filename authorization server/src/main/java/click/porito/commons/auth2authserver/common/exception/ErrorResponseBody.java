package click.porito.commons.auth2authserver.common.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public final class ErrorResponseBody {
    private final String message;
    private final String code;
    private final Map<String,Object> detail;

    public ErrorResponseBody(String message, ErrorCode code, Map<String, Object> detail) {
        this.message = message;
        this.code = code.getCompactCode();
        this.detail = detail;
    }

}
