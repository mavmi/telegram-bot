package mavmi.telegram_bot.shakal.service.shakal.menuHandlers.utils;

import com.pengrad.telegrambot.model.request.ParseMode;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.shakal.telegramBot.client.ShakalTelegramBotSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TelegramBotUtils {

    private final ShakalTelegramBotSender sender;

    public void sendText(long chatId, String msg) {
        sender.sendText(chatId, msg);
    }

    public void sendTextDeleteKeyboard(long chatId, String msg) {
        sender.sendTextDeleteKeyboard(chatId, msg);
    }

    public void sendReplyKeyboard(long chatId, String msg, String[] keyboardButtons) {
        sender.sendReplyKeyboard(chatId, msg, ParseMode.Markdown, keyboardButtons);
    }

    public void sendReplyKeyboard(long chatId, String msg, List<String> keyboardButtons) {
        sender.sendReplyKeyboard(chatId, msg, ParseMode.Markdown, keyboardButtons);
    }

    public void sendDice(long chatId, String msg, String[] keyboardButtons) {
        sender.sendDice(chatId, msg, keyboardButtons);
    }

    public void sendDice(long chatId, String msg, List<String> keyboardButtons) {
        sender.sendDice(chatId, msg, keyboardButtons);
    }
}
