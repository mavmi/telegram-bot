package mavmi.telegram_bot.shakal.service.serviceComponents.serviceModule.common;

import lombok.Getter;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.database.repository.RequestRepository;
import mavmi.telegram_bot.common.database.repository.UserRepository;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.shakal.cache.ShakalDataCache;
import mavmi.telegram_bot.shakal.constantsHandler.ShakalConstantsHandler;
import mavmi.telegram_bot.shakal.constantsHandler.dto.ShakalConstants;
import mavmi.telegram_bot.shakal.telegramBot.client.ShakalTelegramBotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CommonServiceModule {

    private final ShakalTelegramBotSender sender;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final ShakalConstants constants;

    @Autowired
    private CacheComponent cacheComponent;

    public CommonServiceModule(
            ShakalTelegramBotSender sender,
            UserRepository userRepository,
            RequestRepository requestRepository,
            ShakalConstantsHandler constantsHandler
    ) {
        this.sender = sender;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.constants = constantsHandler.get();
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
        DataCache dataCache = getCacheComponent().getCacheBucket().getDataCache(ShakalDataCache.class);

        Menu parentMenu = dataCache.getMenu().getParent();
        if (parentMenu != null) {
            dataCache.setMenu(parentMenu);
        }
    }
}
