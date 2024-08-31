package mavmi.telegram_bot.rocketchat.service.serviceModule.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.cache.api.inner.MenuContainer;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.database.repository.RocketchatRepository;
import mavmi.telegram_bot.common.service.dto.common.DeleteMessageJson;
import mavmi.telegram_bot.common.service.dto.common.ImageJson;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.ROCKETCHAT_SERVICE_TASK;
import mavmi.telegram_bot.rocketchat.cache.RocketchatServiceDataCache;
import mavmi.telegram_bot.rocketchat.constantsHandler.RocketchatServiceConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketchatServiceConstants;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.mapper.RocketchatMapper;
import mavmi.telegram_bot.rocketchat.mapper.WebsocketClientMapper;
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

import java.util.List;

@Slf4j
@Getter
@Component
public class CommonServiceModule {

    private final CryptoMapper cryptoMapper;
    private final TextEncryptor textEncryptor;
    private final RocketchatServiceConstants constants;
    private final RocketchatWebsocketClientBuilder websocketClientBuilder;
    private final RocketchatRepository rocketchatRepository;
    private final WebsocketClientMapper websocketClientMapper;
    private final RocketchatMapper rocketchatMapper;
    private final String outputDirectoryPath;
    private final Long deleteAfterMillisQr;
    private final Long deleteAfterMillisNotification;
    private final String qrCommand;

    public CommonServiceModule(
            CryptoMapper cryptoMapper,
            @Qualifier("rocketChatTextEncryptor") TextEncryptor textEncryptor,
            RocketchatServiceConstantsHandler constantsHandler,
            RocketchatWebsocketClientBuilder websocketClientBuilder,
            RocketchatRepository rocketchatRepository,
            WebsocketClientMapper websocketClientMapper,
            RocketchatMapper rocketchatMapper,
            @Value("${service.output-directory}") String outputDirectoryPath,
            @Value("${service.delete-after-millis.qr}") Long deleteAfterMillisQr,
            @Value("${service.delete-after-millis.notification}") Long deleteAfterMillisNotification,
            @Value("${service.commands.commands-list.qr}") String qrCommand
    ) {
        this.cryptoMapper = cryptoMapper;
        this.textEncryptor = textEncryptor;
        this.constants = constantsHandler.get();
        this.websocketClientBuilder = websocketClientBuilder;
        this.rocketchatRepository = rocketchatRepository;
        this.websocketClientMapper = websocketClientMapper;
        this.rocketchatMapper = rocketchatMapper;
        this.outputDirectoryPath = outputDirectoryPath;
        this.deleteAfterMillisQr = deleteAfterMillisQr;
        this.qrCommand = qrCommand;
        this.deleteAfterMillisNotification = deleteAfterMillisNotification;
    }

    @Autowired
    private CacheComponent cacheComponent;

    public RocketchatServiceRs error(RocketchatServiceRq request) {
        return createUnknownCommandResponse();
    }

    public RocketchatServiceRs createResponse(
            @Nullable String textMessage,
            @Nullable String imagePath,
            Integer msgIdToDelete,
            Long deleteAfterMillis,
            List<ROCKETCHAT_SERVICE_TASK> tasks
    ) {
        MessageJson messageJson = null;
        if (textMessage != null) {
            messageJson = MessageJson
                    .builder()
                    .textMessage(textMessage)
                    .build();
        }

        ImageJson imageJson = null;
        if (imagePath != null) {
            imageJson = ImageJson
                    .builder()
                    .filePath(imagePath)
                    .build();
        }

        DeleteMessageJson deleteMessageJson = DeleteMessageJson
                .builder()
                .msgId(msgIdToDelete)
                .deleteAfterMillis(deleteAfterMillis)
                .build();

        return RocketchatServiceRs
                .builder()
                .rocketchatServiceTasks(tasks)
                .messageJson(messageJson)
                .imageJson(imageJson)
                .deleteMessageJson(deleteMessageJson)
                .build();
    }

    public RocketchatServiceRs createSendTextResponse(String msg) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        return RocketchatServiceRs
                .builder()
                .rocketchatServiceTasks(List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT))
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
                .rocketchatServiceTasks(List.of(ROCKETCHAT_SERVICE_TASK.SEND_IMAGE))
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
                .rocketchatServiceTasks(List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT))
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
                .rocketchatServiceTasks(List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT))
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

    @Nullable SendCommandRs getSendCommandResponse(String msg) {
        return convertStringMessageToDto(msg, SendCommandRs.class);
    }

    @Nullable
    private <T> T convertStringMessageToDto(String msg, Class<T> cls) {
        try {
            return new ObjectMapper().readValue(msg, cls);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
