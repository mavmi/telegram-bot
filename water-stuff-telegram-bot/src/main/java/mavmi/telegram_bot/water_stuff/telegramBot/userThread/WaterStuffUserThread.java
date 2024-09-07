package mavmi.telegram_bot.water_stuff.telegramBot.userThread;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.telegramBot.userThread.UserThread;
import mavmi.telegram_bot.water_stuff.mapper.RequestsMapper;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRs;
import mavmi.telegram_bot.water_stuff.service.water_stuff.WaterStuffDirectService;
import mavmi.telegram_bot.water_stuff.telegramBot.WaterStuffTelegramBotSender;

import java.util.ArrayDeque;
import java.util.Queue;

@Slf4j
@RequiredArgsConstructor
public class WaterStuffUserThread implements UserThread {

    private final WaterStuffUserThreads userThreads;
    private final RequestsMapper requestsMapper;
    private final WaterStuffDirectService waterStuffService;
    private final WaterStuffTelegramBotSender sender;
    private final long chatId;
    private final Queue<Update> updateQueue = new ArrayDeque<>();

    @Override
    public void add(Update update) {
        updateQueue.add(update);
    }

    @Override
    public void run() {
        while (!updateQueue.isEmpty()) {
            Update update = updateQueue.remove();
            Message message = update.message();
            CallbackQuery callbackQuery = update.callbackQuery();

            log.info("Got request from id {}", chatId);

            WaterStuffServiceRq waterStuffServiceRq;
            if (message != null) {
                waterStuffServiceRq = requestsMapper.telegramRequestToWaterStuffServiceRequest(update.message());
            } else {
                waterStuffServiceRq = requestsMapper.telegramCallBackQueryToWaterStuffServiceRequest(callbackQuery);
            }

            WaterStuffServiceRs waterStuffServiceRs = waterStuffService.handleRequest(waterStuffServiceRq);
            if (waterStuffServiceRs == null) {
                continue;
            }

            switch (waterStuffServiceRs.getWaterStuffServiceTask()) {
                case SEND_TEXT -> sender.sendText(chatId, waterStuffServiceRs.getMessageJson().getTextMessage());
                case SEND_TEXT_DELETE_KEYBOARD ->
                        sender.sendTextMessage(chatId, waterStuffServiceRs.getMessageJson().getTextMessage(), ParseMode.Markdown, new ReplyKeyboardRemove());
                case SEND_REPLY_KEYBOARD -> sender.sendReplyKeyboard(
                        chatId,
                        waterStuffServiceRs.getMessageJson().getTextMessage(),
                        waterStuffServiceRs.getReplyKeyboardJson().getKeyboardButtons()
                );
                case SEND_INLINE_KEYBOARD -> sender.sendInlineKeyboard(
                        chatId,
                        waterStuffServiceRs.getMessageJson().getTextMessage(),
                        waterStuffServiceRs.getUpdateMessageJson().getMessageId(),
                        waterStuffServiceRs.getUpdateMessageJson().isUpdate(),
                        waterStuffServiceRs.getInlineKeyboardJson()
                );
            }
        }

        userThreads.removeThread(chatId);
    }
}
