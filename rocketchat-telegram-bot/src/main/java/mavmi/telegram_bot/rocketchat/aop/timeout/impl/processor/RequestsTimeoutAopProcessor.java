package mavmi.telegram_bot.rocketchat.aop.timeout.impl.processor;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.rocketchat.aop.timeout.api.RequestsTimeout;
import mavmi.telegram_bot.rocketchat.aop.timeout.impl.properties.CommandToProxy;
import mavmi.telegram_bot.rocketchat.aop.timeout.impl.properties.CommandsToProxy;
import mavmi.telegram_bot.rocketchat.cache.RocketDataCache;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.Commands;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.command.Command;
import mavmi.telegram_bot.rocketchat.constantsHandler.RocketConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketConstants;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AOP processor for {@link RequestsTimeout}
 */
@Slf4j
@Aspect
@Component
public class RequestsTimeoutAopProcessor {

    private final CommandsToProxy commandsToProxy;
    private final RocketConstants constants;
    private final CommonServiceModule commonServiceModule;

    @Autowired
    private CacheComponent cacheComponent;

    public RequestsTimeoutAopProcessor(
            CommandsToProxy commandsToProxy,
            CommonServiceModule commonServiceModule,
            RocketConstantsHandler constantsHandler
    ) {
        this.commandsToProxy = commandsToProxy;
        this.commonServiceModule = commonServiceModule;
        this.constants = constantsHandler.get();
    }

    @Around("@annotation(requestsTimeout)")
    public Object handle(ProceedingJoinPoint joinPoint, RequestsTimeout requestsTimeout) throws Throwable {
        RocketDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(RocketDataCache.class);
        if (dataCache == null) {
            log.warn("DataCache value is null");
            return joinPoint.proceed();
        }

        RocketchatServiceRq request = (RocketchatServiceRq) joinPoint.getArgs()[0];
        MessageJson messageJson = request.getMessageJson();
        if (messageJson == null) {
            log.warn("Message json is null");
            return null;
        }
        String textMessage = messageJson.getTextMessage();
        CommandToProxy commandToProxy = commandsToProxy.getCommandByName(textMessage);
        if (commandToProxy == null) {
            return joinPoint.proceed();
        }

        Commands commands = dataCache.getCommands();
        Command dataCommand = commands.getCommandByName(textMessage);
        if (dataCommand == null) {
            commands.putCommand(textMessage, System.currentTimeMillis());
            return joinPoint.proceed();
        }

        if (System.currentTimeMillis() - dataCommand.getTimestampMillis() > commandToProxy.getTimeoutSec() * 1000) {
            dataCommand.setTimestampMillis(System.currentTimeMillis());
            return joinPoint.proceed();
        }

        commonServiceModule.sendText(request.getChatId(), constants.getPhrases().getQr().getRequestsTimeout());
        return null;
    }
}
