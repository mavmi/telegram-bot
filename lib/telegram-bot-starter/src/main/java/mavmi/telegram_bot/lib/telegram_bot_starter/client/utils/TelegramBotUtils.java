package mavmi.telegram_bot.lib.telegram_bot_starter.client.utils;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import lombok.experimental.UtilityClass;
import mavmi.telegram_bot.lib.dto.service.common.InlineKeyboardJson;
import mavmi.telegram_bot.lib.dto.service.common.inlineKeyboard.InlineKeyboardButtonJson;
import mavmi.telegram_bot.lib.dto.service.common.inlineKeyboard.InlineKeyboardRowJson;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class TelegramBotUtils {

    public Keyboard toReplyKeyboard(String[] buttons) {
        return toReplyKeyboard(Arrays.asList(buttons));
    }

    public Keyboard toReplyKeyboard(List<String> buttons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .resizeKeyboard(true)
                .oneTimeKeyboard(true);

        for (String button : buttons) {
            replyKeyboardMarkup.addRow(button);
        }

        return replyKeyboardMarkup;
    }

    public InlineKeyboardMarkup toInlineKeyboard(InlineKeyboardJson inlineKeyboardJson) {
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

        return inlineKeyboardMarkup;
    }
}
