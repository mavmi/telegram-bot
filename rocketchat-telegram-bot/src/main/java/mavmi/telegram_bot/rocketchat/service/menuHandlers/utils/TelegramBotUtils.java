package mavmi.telegram_bot.rocketchat.service.menuHandlers.utils;

import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mavmi.telegram_bot.lib.telegram_bot_starter.client.TelegramBotSender;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.rocketchat.cache.dto.RocketDataCache;
import mavmi.telegram_bot.rocketchat.cache.dto.inner.dataCache.MessagesToDelete;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class TelegramBotUtils {

    private final TelegramBotSender sender;

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
}
