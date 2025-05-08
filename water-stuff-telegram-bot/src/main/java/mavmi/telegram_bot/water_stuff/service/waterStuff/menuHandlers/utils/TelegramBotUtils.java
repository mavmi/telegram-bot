package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.common.InlineKeyboardJson;
import mavmi.telegram_bot.lib.telegram_bot_starter.client.TelegramBotSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramBotUtils {

    private final TelegramBotSender sender;
    
    public void sendText(long chatId, String msg) {
        sender.sendTextMessage(chatId, msg, ParseMode.Markdown);
    }

    public void sendTextDeleteKeyboard(long chatId, String msg) {
        sender.sendReplyKeyboard(chatId, msg, new ReplyKeyboardRemove());
    }

    public void sendReplyKeyboard(long chatId, String msg, String[] keyboardButtons) {
        sender.sendReplyKeyboard(chatId, msg, ParseMode.Markdown, keyboardButtons);
    }

    public void sendInlineKeyboard(long chatId, String msg, InlineKeyboardJson inlineKeyboardJson) {
        sender.sendInlineKeyboard(chatId, msg, inlineKeyboardJson);
    }

    public void updateInlineKeyboard(long chatId, int msgId, String msg, InlineKeyboardJson inlineKeyboardJson) {
        sender.updateInlineKeyboard(chatId, msgId, msg, inlineKeyboardJson);
    }
}
