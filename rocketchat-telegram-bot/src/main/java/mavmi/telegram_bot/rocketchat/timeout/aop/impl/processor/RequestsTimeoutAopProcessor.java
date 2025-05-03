package mavmi.telegram_bot.rocketchat.timeout.aop.impl.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.TelegramBotUtils;
import mavmi.telegram_bot.rocketchat.timeout.aop.api.RequestsTimeout;
import mavmi.telegram_bot.rocketchat.timeout.aop.impl.properties.CommandToProxy;
import mavmi.telegram_bot.rocketchat.timeout.aop.impl.properties.CommandsToProxy;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.cache.dto.inner.dataCache.Commands;
import mavmi.telegram_bot.rocketchat.cache.dto.inner.dataCache.command.Command;
import mavmi.telegram_bot.rocketchat.constantsHandler.RocketConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketConstants;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.CommonUtils;
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
@RequiredArgsConstructor
public class RequestsTimeoutAopProcessor {

    private final CommandsToProxy commandsToProxy;
    private final CommonUtils commonUtils;
    private final TelegramBotUtils telegramBotUtils;

    private RocketConstants constants;

    @Autowired
    public void setup(RocketConstantsHandler constantsHandler) {
        this.constants = constantsHandler.get();
    }

    @Around("@annotation(requestsTimeout)")
    public Object handle(ProceedingJoinPoint joinPoint, RequestsTimeout requestsTimeout) throws Throwable {
        UserCaches userCaches = commonUtils.getUserCaches();
        RocketDataCache dataCache = userCaches.getDataCache(RocketDataCache.class);
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

        telegramBotUtils.sendText(request.getChatId(), constants.getPhrases().getQr().getRequestsTimeout());
        return null;
    }
}
