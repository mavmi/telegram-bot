package mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.utils;

import com.pengrad.telegrambot.model.request.ParseMode;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.monitoring.telegramBot.client.MonitoringTelegramBotSender;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TelegramBotUtils {

    private final MonitoringTelegramBotSender sender;

    public void sendText(long chatId, String msg) {
        sender.sendText(chatId, msg);
    }

    public void sendReplyKeyboard(long chatId, String msg, String[] keyboard) {
        sender.sendReplyKeyboard(chatId, msg, ParseMode.Markdown, keyboard);
    }

    public void sendReplyKeyboard(long chatId, String msg, List<String> keyboard) {
        sender.sendReplyKeyboard(chatId, msg, ParseMode.Markdown, keyboard);
    }

    public void sendFile(long chatId, File file) {
        sender.sendFile(chatId, file);
    }
}
