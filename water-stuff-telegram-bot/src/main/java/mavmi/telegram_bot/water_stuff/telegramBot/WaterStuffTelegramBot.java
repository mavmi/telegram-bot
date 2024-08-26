package mavmi.telegram_bot.water_stuff.telegramBot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.service.dto.common.InlineKeyboardJson;
import mavmi.telegram_bot.common.service.dto.common.inlineKeyboard.InlineKeyboardButtonJson;
import mavmi.telegram_bot.common.service.dto.common.inlineKeyboard.InlineKeyboardRowJson;
import mavmi.telegram_bot.common.telegramBot.TelegramBot;
import mavmi.telegram_bot.water_stuff.mapper.RequestsMapper;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRs;
import mavmi.telegram_bot.water_stuff.service.water_stuff.WaterStuffDirectService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class WaterStuffTelegramBot extends TelegramBot {

    private final RequestsMapper requestsMapper;
    private final WaterStuffDirectService waterStuffService;

    public WaterStuffTelegramBot(
            RequestsMapper requestsMapper,
            WaterStuffDirectService waterStuffService,
            @Value("${telegram-bot.token}") String telegramBotToken
    ) {
        super(telegramBotToken);
        this.requestsMapper = requestsMapper;
        this.waterStuffService = waterStuffService;
    }

    @Override
    @PostConstruct
    public void run() {
        telegramBot.setUpdatesListener(new UpdatesListener() {
            @Override
            @SneakyThrows
            public int process(List<Update> updates) {
                for (Update update : updates) {
                    long chatId;
                    Message message = update.message();
                    CallbackQuery callbackQuery = update.callbackQuery();

                    if (message != null) {
                        chatId = message.chat().id();
                    } else {
                        chatId = callbackQuery.maybeInaccessibleMessage().chat().id();
                    }

                    log.info("Got request from id {}", chatId);

                    WaterStuffServiceRq waterStuffServiceRq;
                    if (message != null) {
                        waterStuffServiceRq = requestsMapper.telegramRequestToWaterStuffServiceRequest(update.message());
                    } else {
                        waterStuffServiceRq = requestsMapper.telegramCallBackQueryToWaterStuffServiceRequest(callbackQuery);
                    }

                    CompletableFuture
                            .supplyAsync(() -> {
                                return waterStuffService.handleRequest(waterStuffServiceRq);
                            }).thenApply(arg -> {
                                WaterStuffServiceRs waterStuffServiceRs = (WaterStuffServiceRs) arg;
                                if (waterStuffServiceRs == null) {
                                    return null;
                                }

                                switch (waterStuffServiceRs.getWaterStuffServiceTask()) {
                                    case SEND_TEXT -> sendText(chatId, waterStuffServiceRs.getMessageJson().getTextMessage());
                                    case SEND_REPLY_KEYBOARD -> sendReplyKeyboard(
                                            chatId,
                                            waterStuffServiceRs.getMessageJson().getTextMessage(),
                                            waterStuffServiceRs.getReplyKeyboardJson().getKeyboardButtons()
                                    );
                                    case SEND_INLINE_KEYBOARD -> sendInlineKeyboard(
                                            chatId,
                                            waterStuffServiceRs.getMessageJson().getTextMessage(),
                                            waterStuffServiceRs.getUpdateMessageJson().getMessageId(),
                                            waterStuffServiceRs.getUpdateMessageJson().isUpdate(),
                                            waterStuffServiceRs.getInlineKeyboardJson()
                                    );
                                }
                                return waterStuffServiceRs;
                            }).join();
                }

                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        }, e -> {
            log.error(e.getMessage(), e);
        });
    }

    public void sendText(long chatId, String msg) {
        sendTextMessage(chatId, msg, ParseMode.Markdown);
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
