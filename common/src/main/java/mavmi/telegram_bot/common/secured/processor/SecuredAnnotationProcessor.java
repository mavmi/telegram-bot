package mavmi.telegram_bot.common.secured.processor;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.common.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.secured.annotation.Secured;
import mavmi.telegram_bot.common.secured.exception.SecuredException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Lazy
@Slf4j
@Aspect
@Component
@ConditionalOnBean(UserAuthentication.class)
public class SecuredAnnotationProcessor {

    private final UserAuthentication userAuthentication;

    public SecuredAnnotationProcessor(UserAuthentication userAuthentication) {
        this.userAuthentication = userAuthentication;
    }

    @Around("@annotation(mavmi.telegram_bot.common.secured.annotation.Secured)")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Secured secured = method.getAnnotation(Secured.class);
        Object[] args = joinPoint.getArgs();
        int argsCount = args.length;

        if (argsCount == 1 && args[0] instanceof BotRequestJson) {
            processBotRequest(secured, (BotRequestJson) args[0]);
        } else {
            throw new SecuredException("Invalid arguments");
        }

        return joinPoint.proceed();
    }

    private void processBotRequest(Secured secured, BotRequestJson botRequestJson) {
        Long id = botRequestJson.getChatId();

        if (id == null || !userAuthentication.isPrivilegeGranted(id, secured.value())) {
            log.warn("User unauthorized: id {}", id);
            throw new SecuredException("Unauthorized");
        } else {
            log.info("Access granted for id {}", id);
        }
    }
}
