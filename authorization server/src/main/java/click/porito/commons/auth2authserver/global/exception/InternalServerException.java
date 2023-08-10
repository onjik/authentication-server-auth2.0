package click.porito.commons.auth2authserver.global.exception;

import java.util.logging.Level;

/**
 * 개발자의 실수로 인해 발생하는 예외를 나타내는 클래스 입니다.
 */
public abstract class InternalServerException extends RuntimeException {

    public abstract Level getLogLevel();

    public ErrorCode getErrorCode() {
        return ErrorCode.UNEXPECTED_SERVER_ERROR;
    }


}
