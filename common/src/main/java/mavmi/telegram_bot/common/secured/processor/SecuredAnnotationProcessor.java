package mavmi.telegram_bot.common.secured.processor;

import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.common.dto.json.bot.BotRequestJson;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Lazy
@Aspect
@Component
@ConditionalOnBean(UserAuthentication.class)
public class SecuredAnnotationProcessor {

    private final UserAuthentication userAuthentication;

    public SecuredAnnotationProcessor(UserAuthentication userAuthentication) {
        this.userAuthentication = userAuthentication;
    }

    @Around("@annotation(mavmi.telegram_bot.common.secured.annotation.Secured)")
    public Object process(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();

        System.out.println("XYU GOVNA: " + args.length);
        System.out.println(args[0] instanceof BotRequestJson);

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
