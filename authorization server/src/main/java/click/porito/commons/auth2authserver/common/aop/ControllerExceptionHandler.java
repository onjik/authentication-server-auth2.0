package click.porito.commons.auth2authserver.common.aop;

import click.porito.commons.auth2authserver.common.exception.ErrorResponseBody;
import click.porito.commons.auth2authserver.common.exception.InternalServerException;
import click.porito.commons.auth2authserver.common.infra.dev_notifier.DeveloperNotifier;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    private final DeveloperNotifier developerNotifier;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<List<FieldError>> handle(BindException e){
        BindingResult bindingResult = e.getBindingResult();
        return ResponseEntity.ok(bindingResult.getFieldErrors());
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponseBody> handleInternalServerException(InternalServerException e){
        developerNotifier.notify(e.getMessage(), e.getLogLevel());
        ErrorResponseBody body = new ErrorResponseBody(e.getMessage(), e.getErrorCode(), null);
        return ResponseEntity.internalServerError().body(body);
    }

}
