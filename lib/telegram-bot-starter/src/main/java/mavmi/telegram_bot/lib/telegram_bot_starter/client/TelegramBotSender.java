package mavmi.telegram_bot.lib.telegram_bot_starter.client;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.common.InlineKeyboardJson;
import mavmi.telegram_bot.lib.telegram_bot_starter.client.utils.TelegramBotUtils;

import java.io.File;
import java.util.List;

@RequiredArgsConstructor
public abstract class TelegramBotSender {

    protected final TelegramBot telegramBot;

    synchronized public <T extends BaseRequest<T, R>, R extends BaseResponse> R sendRequest(BaseRequest<T, R> baseRequest) {
        return telegramBot.execute(baseRequest);
    }

    synchronized public BaseResponse sendAnswerCallbackQuery(AnswerCallbackQuery answerCallbackQuery) {
        return telegramBot.execute(answerCallbackQuery);
    }

    synchronized public SendResponse sendMessage(SendMessage sendMessage) {
        return sendRequest(sendMessage);
    }

    synchronized public SendResponse sendTextMessage(long chatId, String msg) {
        return sendMessage(new SendMessage(chatId, msg));
    }

    synchronized public SendResponse sendTextMessage(long chatId, String msg, ParseMode parseMode) {
        return sendMessage(new SendMessage(chatId, msg)
                .parseMode(parseMode));
    }

    synchronized public SendResponse sendReplyKeyboard(long chatId, String msg, String[] keyboard) {
        return sendReplyKeyboard(chatId, msg, TelegramBotUtils.toReplyKeyboard(keyboard));
    }

    synchronized public SendResponse sendReplyKeyboard(long chatId, String msg, List<String> keyboard) {
        return sendReplyKeyboard(chatId, msg, TelegramBotUtils.toReplyKeyboard(keyboard));
    }

    synchronized public SendResponse sendReplyKeyboard(long chatId, String msg, Keyboard keyboard) {
        return sendMessage(new SendMessage(chatId, msg).replyMarkup(keyboard));
    }

    synchronized public SendResponse sendReplyKeyboard(long chatId, String msg, ParseMode parseMode, String[] keyboard) {
        return sendReplyKeyboard(chatId, msg, parseMode, TelegramBotUtils.toReplyKeyboard(keyboard));
    }

    synchronized public SendResponse sendReplyKeyboard(long chatId, String msg, ParseMode parseMode, List<String> keyboard) {
        return sendReplyKeyboard(chatId, msg, parseMode, TelegramBotUtils.toReplyKeyboard(keyboard));
    }

    synchronized public SendResponse sendReplyKeyboard(long chatId, String msg, ParseMode parseMode, Keyboard keyboard) {
        return sendMessage(new SendMessage(chatId, msg)
                .parseMode(parseMode)
                .replyMarkup(keyboard));
    }

    synchronized public void sendInlineKeyboard(long chatId, String message, InlineKeyboardJson inlineKeyboardJson) {
        InlineKeyboardMarkup keyboard = TelegramBotUtils.toInlineKeyboard(inlineKeyboardJson);
        telegramBot.execute(new SendMessage(chatId, message).replyMarkup(keyboard));
    }

    synchronized public void updateInlineKeyboard(long chatId, int msgId, String message, InlineKeyboardJson inlineKeyboardJson) {
        InlineKeyboardMarkup keyboard = TelegramBotUtils.toInlineKeyboard(inlineKeyboardJson);
        telegramBot.execute(new EditMessageText(chatId, msgId, message).replyMarkup(keyboard));
    }

    synchronized public SendResponse sendFile(long chatId, File file) {
        return sendRequest(new SendDocument(chatId, file));
    }

    synchronized public SendResponse sendImage(long chatId, File file) {
        return sendRequest(new SendPhoto(chatId, file));
    }

    synchronized public SendResponse sendImage(long chatId, File file, String textMessage) {
        return sendRequest(new SendPhoto(chatId, file).caption(textMessage));
    }

    synchronized public SendResponse sendDice(long chatId) {
        return sendRequest(new SendDice(chatId));
    }

    synchronized public BaseResponse deleteMessage(long chatId, int msgId) {
        return sendRequest(new DeleteMessage(chatId, msgId));
    }
}
