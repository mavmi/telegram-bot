package mavmi.telegram_bot.rocketchat.service.rocketchat.menuHandlers.utils;

import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.telegram_bot_starter.client.TelegramBotSender;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TelegramBotUtils {

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    private final TelegramBotSender sender;

    public int sendText(long chatId, String msg) {
        return sender.sendTextMessage(chatId, msg).message().messageId();
    }

    public int sendTextDeleteKeyboard(long chatId, String msg) {
        return sender.sendReplyKeyboard(chatId, msg, new ReplyKeyboardRemove()).message().messageId();
    }

    public int sendImage(long chatId, String textMsg, File imageFile) {
        return sender.sendImage(chatId, imageFile, textMsg).message().messageId();
    }

    public void deleteMessage(long chatId, int msgId) {
        sender.deleteMessage(chatId, msgId);
    }

    public void deleteQueuedMessages(long chatId, UserCaches userCaches) {
        RocketDataCache dataCache = userCaches.getDataCache(RocketDataCache.class);

        while (dataCache.messagesToDeleteSize() != 0) {
            int msgId = dataCache.removeCommandToDelete();
            deleteMessage(chatId, msgId);
        }
    }

    public void deleteMessageAfterMillis(long chatId, int msgId, long millis) {
        executorService.schedule(() -> deleteMessage(chatId, msgId),
                millis,
                TimeUnit.MILLISECONDS);
    }
}
