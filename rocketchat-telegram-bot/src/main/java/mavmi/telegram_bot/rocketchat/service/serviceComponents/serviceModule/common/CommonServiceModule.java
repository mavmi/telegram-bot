package mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.parameters_management_system.client.plugin.impl.remote.RemoteParameterPlugin;
import mavmi.telegram_bot.lib.database_starter.repository.LogsWebsocketRepository;
import mavmi.telegram_bot.lib.database_starter.repository.RocketchatRepository;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.dto.service.common.tasks.ROCKETCHAT_SERVICE_TASK;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.rocketchat.cache.RocketDataCache;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.MessagesToDelete;
import mavmi.telegram_bot.rocketchat.constantsHandler.RocketConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketConstants;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.mapper.WebsocketClientMapper;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.*;
import mavmi.telegram_bot.rocketchat.telegramBot.client.RocketTelegramBotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class CommonServiceModule {

    private final UserCachesProvider userCachesProvider;
    private final RemoteParameterPlugin parameterPlugin;
    private final RocketTelegramBotSender sender;
    private final CryptoMapper cryptoMapper;
    private final LogsWebsocketRepository logsWebsocketRepository;
    private final RocketchatRepository rocketchatRepository;
    private final WebsocketClientMapper websocketClientMapper;

    private RocketConstants constants;
    private TextEncryptor textEncryptor;
    private String outputDirectoryPath;
    private String qrCommand;
    private String rocketchatUrl;

    @Autowired
    public void setup(@Qualifier("rocketChatTextEncryptor") TextEncryptor textEncryptor,
                      @Value("${service.output-directory}") String outputDirectoryPath,
                      @Value("${service.commands.commands-list.qr}") String qrCommand,
                      @Value("${websocket.client.url}") String rocketchatUrl,
                      RocketConstantsHandler constantsHandler) {
        this.constants = constantsHandler.get();
        this.textEncryptor = textEncryptor;
        this.outputDirectoryPath = outputDirectoryPath;
        this.qrCommand = qrCommand;
        this.rocketchatUrl = rocketchatUrl;
    }

    public UserCaches getUserCaches() {
        return userCachesProvider.get();
    }

    public long getConnectionTimeout() {
        return parameterPlugin.getParameter("rocket.websocket.client.timeout-sec").getLong();
    }

    public long getAwaitingPeriodMillis() {
        return parameterPlugin.getParameter("rocket.websocket.client.awaiting-period-millis").getLong();
    }

    public long getDeleteAfterMillisNotification() {
        return parameterPlugin.getParameter("rocket.service.delete-after-millis.notification").getLong();
    }

    public long getDeleteAfterMillisQr() {
        return parameterPlugin.getParameter("rocket.service.delete-after-millis.qr").getLong();
    }

    public int sendText(long chatId, String msg) {
        return sender.sendTextMessage(chatId, msg).message().messageId();
    }

    public int sendTextDeleteKeyboard(long chatId, String msg) {
        return sender.sendTextMessage(chatId, msg, new ReplyKeyboardRemove()).message().messageId();
    }

    public int sendImage(long chatId, String textMsg, File imageFile) {
        return sender.sendImage(chatId, imageFile, textMsg).message().messageId();
    }

    public void deleteMessage(long chatId, int msgId) {
        sender.deleteMessage(chatId, msgId);
    }

    public void addMessageToDeleteAfterEnd(int msgId) {
        getUserCaches().getDataCache(RocketDataCache.class)
                .getMessagesToDelete()
                .add(msgId);
    }

    public void addMessageToDeleteAfterEnd(int msgId, UserCaches userCaches) {
        userCaches.getDataCache(RocketDataCache.class)
                .getMessagesToDelete()
                .add(msgId);
    }

    public void deleteQueuedMessages(long chatId) {
        MessagesToDelete msgsToDelete = getUserCaches().getDataCache(RocketDataCache.class)
                .getMessagesToDelete();

        while (msgsToDelete.size() != 0) {
            int msgId = msgsToDelete.remove();
            deleteMessage(chatId, msgId);
        }
    }

    public void deleteQueuedMessages(long chatId, UserCaches userCaches) {
        MessagesToDelete msgsToDelete = userCaches.getDataCache(RocketDataCache.class).getMessagesToDelete();

        while (msgsToDelete.size() != 0) {
            int msgId = msgsToDelete.remove();
            deleteMessage(chatId, msgId);
        }
    }

    public void deleteMessageAfterMillis(long chatId, int msgId, long millis) {
        Thread.ofVirtual()
                .start(new Runnable() {
                    @Override
                    @SneakyThrows
                    public void run() {
                        Thread.sleep(millis);
                        deleteMessage(chatId, msgId);
                    }
                });
    }

    public RocketchatServiceRs error(RocketchatServiceRq request) {
        return createUnknownCommandResponse();
    }

    public RocketchatServiceRs createUnknownCommandResponse() {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(constants.getPhrases().getCommon().getUnknownCommand())
                .build();

        return RocketchatServiceRs
                .builder()
                .rocketchatServiceTasks(List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT))
                .messageJson(messageJson)
                .build();
    }

    public void dropUserMenu() {
        DataCache dataCache = getUserCaches().getDataCache(RocketDataCache.class);
        Menu menu = dataCache.getMenu();

        while (true) {
            Menu parentMenu = menu.getParent();
            if (parentMenu == null) {
                dataCache.setMenu(menu);
                break;
            } else {
                menu = parentMenu;
            }
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
