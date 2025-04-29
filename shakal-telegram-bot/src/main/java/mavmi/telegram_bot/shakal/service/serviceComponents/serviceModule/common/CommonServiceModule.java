package mavmi.telegram_bot.shakal.service.serviceComponents.serviceModule.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.repository.RequestRepository;
import mavmi.telegram_bot.lib.database_starter.repository.UserRepository;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.shakal.cache.ShakalDataCache;
import mavmi.telegram_bot.shakal.constantsHandler.ShakalConstantsHandler;
import mavmi.telegram_bot.shakal.constantsHandler.dto.ShakalConstants;
import mavmi.telegram_bot.shakal.telegramBot.client.ShakalTelegramBotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class CommonServiceModule {

    private final UserCachesProvider userCachesProvider;
    private final ShakalTelegramBotSender sender;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    private ShakalConstants constants;

    @Autowired
    public void setup(ShakalConstantsHandler constantsHandler) {
        this.constants = constantsHandler.get();
    }

    public UserCaches getUserCaches() {
        return userCachesProvider.get();
    }

    public void sendText(long chatId, String msg) {
        sender.sendText(chatId, msg);
    }

    public void sendTextDeleteKeyboard(long chatId, String msg) {
        sender.sendTextDeleteKeyboard(chatId, msg);
    }

    public void sendReplyKeyboard(long chatId, String msg, String[] keyboardButtons) {
        sender.sendReplyKeyboard(chatId, msg, keyboardButtons);
    }

    public void sendDice(long chatId, String msg, String[] keyboardButtons) {
        sender.sendDice(chatId, msg, keyboardButtons);
    }

    public void dropUserCaches() {
        DataCache dataCache = getUserCaches().getDataCache(ShakalDataCache.class);

        Menu parentMenu = dataCache.getMenu().getParent();
        if (parentMenu != null) {
            dataCache.setMenu(parentMenu);
        }
    }
}
