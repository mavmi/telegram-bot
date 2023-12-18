package mavmi.telegram_bot.common.secured.exception;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Lazy
@ControllerAdvice
@ConditionalOnProperty(prefix = "secured", name = "enabled", havingValue = "true")
public class SecuredExceptionHandler {

    @ExceptionHandler(SecuredException.class)
    public ResponseEntity<String> securedException() {
        return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()));
    }
}
