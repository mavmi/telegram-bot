package mavmi.telegram_bot.common.secured.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.api.IRq;
import mavmi.telegram_bot.common.httpFilter.session.UserSession;
import mavmi.telegram_bot.common.secured.annotation.Secured;
import mavmi.telegram_bot.common.secured.exception.SecuredException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "secured", name = "enabled", havingValue = "true")
public class SecuredAnnotationProcessor {

    @Autowired
    private UserSession userSession;

    @Around("@annotation(secured)")
    public Object process(ProceedingJoinPoint joinPoint, Secured secured) throws Throwable {
        Object[] args = joinPoint.getArgs();
        int argsCount = args.length;

        if (argsCount == 1 && args[0] instanceof IRq) {
            processBotRequest();
        } else {
            throw new SecuredException("Invalid arguments");
        }

        return joinPoint.proceed();
    }

    private void processBotRequest() {
        long id = userSession.getId();

        if (userSession.getAccessGranted() == null || !userSession.getAccessGranted()) {
            log.warn("User unauthorized: id {}", id);
            throw new SecuredException("Unauthorized");
        } else {
            log.info("Access granted for id {}", id);
        }
    }
}
