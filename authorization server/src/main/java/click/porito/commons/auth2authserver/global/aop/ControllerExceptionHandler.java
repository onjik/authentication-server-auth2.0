package click.porito.commons.auth2authserver.global.aop;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<List<FieldError>> handle(BindException e){
        BindingResult bindingResult = e.getBindingResult();
        return ResponseEntity.ok(bindingResult.getFieldErrors());

    }

}
