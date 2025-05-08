package mavmi.telegram_bot.shakal.telegramBot.client;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import lombok.SneakyThrows;
import mavmi.telegram_bot.lib.telegram_bot_starter.client.TelegramBotSender;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.shakal.cache.dto.ShakalDataCache;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShakalTelegramBotSender extends TelegramBotSender {

    private final UserCachesProvider userCachesProvider;

    public ShakalTelegramBotSender(UserCachesProvider userCachesProvider,
                                   TelegramBot telegramBot) {
        super(telegramBot);
        this.userCachesProvider = userCachesProvider;
    }

    public void sendText(long chatId, String msg) {
        sendTextMessage(chatId, msg, ParseMode.Markdown);
    }

    public void sendTextDeleteKeyboard(long chatId, String msg) {
        sendReplyKeyboard(chatId, msg, ParseMode.Markdown, new ReplyKeyboardRemove());
    }

    @SneakyThrows
    public void sendDice(long chatId, String msg, String[] keyboardButtons) {
        sendReplyKeyboard(chatId, msg, keyboardButtons);
        int botDiceValue = sendDice(chatId).message().dice().value();
        userCachesProvider.get().getDataCache(ShakalDataCache.class).setBotDice(botDiceValue);
    }

    @SneakyThrows
    public void sendDice(long chatId, String msg, List<String> keyboardButtons) {
        sendReplyKeyboard(chatId, msg, keyboardButtons);
        int botDiceValue = sendDice(chatId).message().dice().value();
        userCachesProvider.get().getDataCache(ShakalDataCache.class).setBotDice(botDiceValue);
    }
}
