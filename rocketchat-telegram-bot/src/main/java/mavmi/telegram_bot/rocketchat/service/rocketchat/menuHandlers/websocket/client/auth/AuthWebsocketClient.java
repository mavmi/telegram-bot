package mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.websocket.client.auth;

import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.service.database.dto.RocketchatDto;
import mavmi.telegram_bot.rocketchat.service.rocketchat.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.PmsUtils;
import mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils.TelegramBotUtils;
import org.springframework.security.crypto.encrypt.TextEncryptor;

public class AuthWebsocketClient extends AbstractAuthWebsocketClient {

    private final AUTH_MODE authMode;

    public AuthWebsocketClient(RocketchatServiceRq request,
                               UserCaches userCaches,
                               CommonUtils commonUtils,
                               TelegramBotUtils telegramBotUtils,
                               PmsUtils pmsUtils,
                               AUTH_MODE authMode) {
        super(request, userCaches, commonUtils, telegramBotUtils, pmsUtils, authMode);
        this.authMode = authMode;
    }

    @Override
    protected void onSuccess(Object... payload) {
        CryptoMapper cryptoMapper = commonUtils.getCryptoMapper();
        TextEncryptor textEncryptor = commonUtils.getTextEncryptor();
        RocketDataCache dataCache = userCaches.getDataCache(RocketDataCache.class);

        long chatId = request.getChatId();
        String rocketchatUsername = dataCache.getRocketchatUsername();
        String rocketchatPasswordHash = dataCache.getRocketchatPasswordHash();
        String rocketchatToken = dataCache.getRocketchatToken();
        RocketchatDto dto = commonUtils.getDatabaseService().findByTelegramId(chatId);

        if (dto == null) {
            RocketchatDto newDto = RocketchatDto.builder()
                    .telegramId(chatId)
                    .telegramUsername(request.getUserJson().getUsername())
                    .telegramFirstname(request.getUserJson().getFirstName())
                    .telegramLastname(request.getUserJson().getLastName())
                    .rocketchatUsername(rocketchatUsername)
                    .rocketchatPasswordHash(rocketchatPasswordHash)
                    .rocketchatToken(rocketchatToken)
                    .build();

            RocketchatDto encryptedDto = cryptoMapper.encryptRocketchatDto(textEncryptor, newDto);
            commonUtils.getDatabaseService().save(encryptedDto);
        } else {
            RocketchatDto decryptedDto = cryptoMapper.decryptRocketchatDto(textEncryptor, dto)
                    .setRocketchatUsername(rocketchatUsername)
                    .setRocketchatPasswordHash(rocketchatPasswordHash)
                    .setRocketchatToken(rocketchatToken);
            RocketchatDto encryptedDto = cryptoMapper.encryptRocketchatDto(textEncryptor, decryptedDto);
            commonUtils.getDatabaseService().updateByTelegramId(encryptedDto);
        }

        String notificationStr = commonUtils.getConstants().getPhrases().getAuth().getAuthSuccess();
        if (rocketchatUsername != null) {
            notificationStr += ": " + rocketchatUsername;
        }

        telegramBotUtils.sendTextDeleteKeyboard(chatId, notificationStr);
        userCaches.getDataCache(RocketDataCache.class).resetCreds();
        commonUtils.dropUserMenu(userCaches);
    }

    @Override
    protected void onFailure(Object... payload) {
        long chatId = request.getChatId();
        String textMsg = constants.getPhrases().getCommon().getError()
                + "\n"
                + loginResponse.getError().getMessage();

        int msgId = telegramBotUtils.sendTextDeleteKeyboard(chatId, textMsg);
        telegramBotUtils.deleteMessageAfterMillis(chatId, msgId, pmsUtils.getDeleteAfterMillisNotification());
        telegramBotUtils.deleteQueuedMessages(chatId, userCaches);
        userCaches.getDataCache(RocketDataCache.class).resetCreds();
        commonUtils.dropUserMenu(userCaches);
    }
}
