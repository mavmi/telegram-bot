package mavmi.telegram_bot.rocketchat.service.serviceModule.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import mavmi.telegram_bot.common.cache.api.inner.MenuContainer;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.service.dto.common.ImageJson;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.ROCKETCHAT_SERVICE_TASK;
import mavmi.telegram_bot.rocketchat.cache.RocketchatServiceDataCache;
import mavmi.telegram_bot.rocketchat.constantsHandler.RocketchatServiceConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketchatServiceConstants;
import mavmi.telegram_bot.rocketchat.httpClient.RocketchatHttpClient;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.*;
import mavmi.telegram_bot.rocketchat.service.menu.RocketchatServiceMenu;
import mavmi.telegram_bot.rocketchat.websocketClient.RocketchatWebsocketClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CommonServiceModule {

    private final CryptoMapper cryptoMapper;
    private final TextEncryptor textEncryptor;
    private final RocketchatServiceConstants constants;
    private final RocketchatWebsocketClientBuilder websocketClientBuilder;
    private final String outputDirectoryPath;

    public CommonServiceModule(
            CryptoMapper cryptoMapper,
            @Qualifier("rocketChatTextEncryptor") TextEncryptor textEncryptor,
            RocketchatServiceConstantsHandler constantsHandler,
            RocketchatWebsocketClientBuilder websocketClientBuilder,
            @Value("${service.output-directory}") String outputDirectoryPath
    ) {
        this.cryptoMapper = cryptoMapper;
        this.textEncryptor = textEncryptor;
        this.constants = constantsHandler.get();
        this.websocketClientBuilder = websocketClientBuilder;
        this.outputDirectoryPath = outputDirectoryPath;
    }

    @Autowired
    private CacheComponent cacheComponent;
    @Autowired
    private RocketchatHttpClient rocketchatHttpClient;

    public RocketchatServiceRs error(RocketchatServiceRq request) {
        return createUnknownCommandResponse();
    }

    public RocketchatServiceRs createSendTextResponse(String msg) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        return RocketchatServiceRs
                .builder()
                .rocketchatServiceTask(ROCKETCHAT_SERVICE_TASK.SEND_TEXT)
                .messageJson(messageJson)
                .build();
    }

    public RocketchatServiceRs createSendImageResponse(String textMessage, String filePath) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(textMessage)
                .build();

        ImageJson imageJson = ImageJson
                .builder()
                .filePath(filePath)
                .build();

        return RocketchatServiceRs
                .builder()
                .rocketchatServiceTask(ROCKETCHAT_SERVICE_TASK.SEND_IMAGE)
                .messageJson(messageJson)
                .imageJson(imageJson)
                .build();
    }

    public RocketchatServiceRs createBadRequestResponse() {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(constants.getPhrases().getInvalidRequest())
                .build();

        return RocketchatServiceRs
                .builder()
                .rocketchatServiceTask(ROCKETCHAT_SERVICE_TASK.SEND_TEXT)
                .messageJson(messageJson)
                .build();
    }

    public RocketchatServiceRs createUnknownCommandResponse() {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(constants.getPhrases().getUnknownCommand())
                .build();

        return RocketchatServiceRs
                .builder()
                .rocketchatServiceTask(ROCKETCHAT_SERVICE_TASK.SEND_TEXT)
                .messageJson(messageJson)
                .build();
    }

    public void dropMenu() {
        MenuContainer menuContainer = cacheComponent.getCacheBucket().getDataCache(RocketchatServiceDataCache.class).getMenuContainer();
        RocketchatServiceMenu menu = (RocketchatServiceMenu) menuContainer.getLast();

        while (!menu.equals(RocketchatServiceMenu.MAIN_MENU)) {
            menuContainer.removeLast();
            menu = (RocketchatServiceMenu) menuContainer.getLast();
        }
    }

    @Nullable
    public ConnectRs getConnectRs(String msg) {
        return convertStringMessageToDto(msg, ConnectRs.class);
    }

    @Nullable
    public LoginRs getLoginRs(String msg) {
        return convertStringMessageToDto(msg, LoginRs.class);
    }

    @Nullable
    public CreateDMRs getCreateDmRs(String msg) {
        return convertStringMessageToDto(msg, CreateDMRs.class);
    }

    @Nullable
    public MessageChangedNotificationRs getMessageChangedNotification(String msg) {
        return convertStringMessageToDto(msg, MessageChangedNotificationRs.class);
    }

    @Nullable
    public SubscribeForMsgUpdatesRs getSubscribeForMsgUpdates(String msg) {
        return convertStringMessageToDto(msg, SubscribeForMsgUpdatesRs.class);
    }

    @Nullable
    private <T> T convertStringMessageToDto(String msg, Class<T> cls) {
        try {
            return new ObjectMapper().readValue(msg, cls);
        } catch (JsonProcessingException e) {
            e.printStackTrace(System.out);
            return null;
        }
    }
}
