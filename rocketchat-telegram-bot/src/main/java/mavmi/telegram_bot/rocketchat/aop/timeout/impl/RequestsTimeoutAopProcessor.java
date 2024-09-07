package mavmi.telegram_bot.rocketchat.aop.timeout.impl;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.ROCKETCHAT_SERVICE_TASK;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.rocketchat.aop.timeout.api.RequestsTimeout;
import mavmi.telegram_bot.rocketchat.cache.RocketchatServiceDataCache;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.RocketchatServiceDataCacheCommand;
import mavmi.telegram_bot.rocketchat.constantsHandler.RocketchatServiceConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketchatServiceConstants;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Aspect
@Component
public class RequestsTimeoutAopProcessor {

    private final CommandsToProxy commandsToProxy;
    private final RocketchatServiceConstants constants;

    @Autowired
    private CacheComponent cacheComponent;

    public RequestsTimeoutAopProcessor(
            CommandsToProxy commandsToProxy,
            RocketchatServiceConstantsHandler constantsHandler
    ) {
        this.commandsToProxy = commandsToProxy;
        this.constants = constantsHandler.get();
    }

    @Around("@annotation(requestsTimeout)")
    public Object handle(ProceedingJoinPoint joinPoint, RequestsTimeout requestsTimeout) throws Throwable {
        RocketchatServiceDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(RocketchatServiceDataCache.class);
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

        RocketchatServiceDataCacheCommand dataCacheCommand = dataCache.getCommandByName(textMessage);
        if (dataCacheCommand == null) {
            dataCache.putCommand(textMessage, System.currentTimeMillis());
            return joinPoint.proceed();
        }

        if (System.currentTimeMillis() - dataCacheCommand.getTimestampMillis() > commandToProxy.getTimeoutSec() * 1000) {
            dataCacheCommand.setTimestampMillis(System.currentTimeMillis());
            return joinPoint.proceed();
        }

        return createOnTimeoutResponse();
    }

    private List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> createOnTimeoutResponse() {
        return List.of(this::onTimeoutMethod);
    }

    private RocketchatServiceRs onTimeoutMethod(RocketchatServiceRq request) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(constants.getPhrases().getRequestsTimeout())
                .build();

        return RocketchatServiceRs
                .builder()
                .rocketchatServiceTasks(List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT))
                .messageJson(messageJson)
                .build();
    }
}
