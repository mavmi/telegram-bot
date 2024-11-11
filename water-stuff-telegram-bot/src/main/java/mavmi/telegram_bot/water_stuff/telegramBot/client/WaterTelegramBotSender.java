package mavmi.telegram_bot.water_stuff.telegramBot.client;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import mavmi.telegram_bot.common.service.dto.common.InlineKeyboardJson;
import mavmi.telegram_bot.common.service.dto.common.inlineKeyboard.InlineKeyboardButtonJson;
import mavmi.telegram_bot.common.service.dto.common.inlineKeyboard.InlineKeyboardRowJson;
import mavmi.telegram_bot.common.telegramBot.client.TelegramBotSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WaterTelegramBotSender extends TelegramBotSender {

    public WaterTelegramBotSender(TelegramBot telegramBot) {
        super(telegramBot);
    }

    public void sendText(long chatId, String msg) {
        sendTextMessage(chatId, msg, ParseMode.Markdown);
    }

    public void sendTextDeleteKeyboard(long chatId, String msg) {
        sendTextMessage(chatId, msg, ParseMode.Markdown, new ReplyKeyboardRemove());
    }

    public void sendReplyKeyboard(long chatId, String msg, String[] keyboardButtons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .oneTimeKeyboard(true)
                .resizeKeyboard(true);

        for (String button : keyboardButtons) {
            replyKeyboardMarkup.addRow(button);
        }

        sendMessage(new SendMessage(chatId, msg)
                .replyMarkup(replyKeyboardMarkup)
                .parseMode(ParseMode.Markdown));
    }

    public void sendInlineKeyboard(long chatId, String message, Integer msgId, boolean update, InlineKeyboardJson inlineKeyboardJson) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        for (InlineKeyboardRowJson inlineKeyboardRowJson : inlineKeyboardJson.getKeyboardButtons()) {
            List<InlineKeyboardButtonJson> inputRow = inlineKeyboardRowJson.getRow();
            int inputRowSize = inputRow.size();
            InlineKeyboardButton[] outputRow = new InlineKeyboardButton[inputRowSize];

            for (int i = 0; i < inputRowSize; i++) {
                InlineKeyboardButtonJson inputButton = inputRow.get(i);
                if (inputButton.getKey() == null || inputButton.getValue() == null) {
                    continue;
                }

                outputRow[i] = new InlineKeyboardButton(inputButton.getKey())
                        .callbackData(inputButton.getValue());
            }

            inlineKeyboardMarkup.addRow(outputRow);
        }

        if (!update) {
            telegramBot.execute(new SendMessage(chatId, message).replyMarkup(inlineKeyboardMarkup));
        } else {
            telegramBot.execute(new EditMessageText(chatId, msgId, message).replyMarkup(inlineKeyboardMarkup));
        }
    }
}
