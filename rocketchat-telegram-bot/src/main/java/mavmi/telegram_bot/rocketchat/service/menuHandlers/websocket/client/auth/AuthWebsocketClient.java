package mavmi.telegram_bot.rocketchat.service.menuHandlers.websocket.client.auth;

import mavmi.telegram_bot.lib.database_starter.model.RocketchatModel;
import mavmi.telegram_bot.lib.database_starter.repository.RocketchatRepository;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.mapper.CryptoMapper;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.CommonUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.PmsUtils;
import mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.TelegramBotUtils;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.util.Optional;

public class AuthWebsocketClient extends AbstractAuthWebsocketClient {

    public AuthWebsocketClient(RocketchatServiceRq request,
                               UserCaches userCaches,
                               CommonUtils commonUtils,
                               TelegramBotUtils telegramBotUtils,
                               PmsUtils pmsUtils) {
        super(request, userCaches, commonUtils, telegramBotUtils, pmsUtils);
    }

    @Override
    protected void onSuccess(Object... payload) {
        RocketchatRepository rocketchatRepository = commonUtils.getRocketchatRepository();
        CryptoMapper cryptoMapper = commonUtils.getCryptoMapper();
        TextEncryptor textEncryptor = commonUtils.getTextEncryptor();
        RocketDataCache dataCache = userCaches.getDataCache(RocketDataCache.class);

        long chatId = request.getChatId();
        String rocketchatUsername = dataCache.getRocketchatUsername();
        String rocketchatPasswordHash = dataCache.getRocketchatPasswordHash();
        String rocketchatToken = loginResponse.getResult().getToken();
        Long rocketchatTokenExpiry = loginResponse.getResult().getTokenExpires().getDate();
        Optional<RocketchatModel> optional = rocketchatRepository.findByTelegramId(chatId);

        if (optional.isEmpty()) {
            RocketchatModel rocketchatModel = RocketchatModel
                    .builder()
                    .telegramId(chatId)
                    .telegramUsername(request.getUserJson().getUsername())
                    .telegramFirstname(request.getUserJson().getFirstName())
                    .telegramLastname(request.getUserJson().getLastName())
                    .rocketchatUsername(rocketchatUsername)
                    .rocketchatPasswordHash(rocketchatPasswordHash)
                    .rocketchatToken(rocketchatToken)
                    .rocketchatTokenExpiryDate(rocketchatTokenExpiry)
                    .build();
            rocketchatModel = cryptoMapper.encryptRocketchatModel(textEncryptor, rocketchatModel);
            rocketchatRepository.save(rocketchatModel);
        } else {
            RocketchatModel model = optional.get();
            model = cryptoMapper.decryptRocketchatModel(textEncryptor, model)
                    .setRocketchatUsername(rocketchatUsername)
                    .setRocketchatPasswordHash(rocketchatPasswordHash)
                    .setRocketchatToken(rocketchatToken)
                    .setRocketchatTokenExpiryDate(rocketchatTokenExpiry);
            model = cryptoMapper.encryptRocketchatModel(textEncryptor, model);

            rocketchatRepository.updateByTelegramId(model);
        }

        telegramBotUtils.sendText(chatId, commonUtils.getConstants().getPhrases().getAuth().getAuthSuccess() + ": " + rocketchatUsername);
    }

    @Override
    protected void onFailure(Object... payload) {
        long chatId = request.getChatId();
        String textMsg = constants.getPhrases().getCommon().getError()
                + "\n"
                + loginResponse.getError().getMessage();

        int msgId = telegramBotUtils.sendText(chatId, textMsg);
        telegramBotUtils.deleteMessageAfterMillis(chatId, msgId, pmsUtils.getDeleteAfterMillisNotification());
        telegramBotUtils.deleteQueuedMessages(chatId, userCaches);
    }
}
