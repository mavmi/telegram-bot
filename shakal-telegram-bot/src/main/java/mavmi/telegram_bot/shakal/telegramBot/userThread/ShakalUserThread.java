package mavmi.telegram_bot.shakal.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.telegramBot.userThread.UserThread;
import mavmi.telegram_bot.shakal.mapper.RequestsMapper;
import mavmi.telegram_bot.shakal.service.ShakalDirectService;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRs;
import mavmi.telegram_bot.shakal.telegramBot.ShakalTelegramBotSender;

import java.util.ArrayDeque;
import java.util.Queue;

@Slf4j
@RequiredArgsConstructor
public class ShakalUserThread implements UserThread {

    private final ShakalUserThreads userThreads;
    private final RequestsMapper requestsMapper;
    private final ShakalDirectService shakalService;
    private final ShakalTelegramBotSender sender;
    private final long chatId;
    private final Queue<Update> updateQueue = new ArrayDeque<>();

    @Override
    public void add(Update update) {
        updateQueue.add(update);
    }

    @Override
    public void run() {
        while (!updateQueue.isEmpty()) {
            Message message = updateQueue.remove().message();
            log.info("Got request from id {}", message.from().id());

            ShakalServiceRq shakalServiceRq = requestsMapper.telegramRequestToShakalServiceRequest(message);
            log.info(shakalServiceRq.toString());
            ShakalServiceRs shakalServiceRs = shakalService.handleRequest(shakalServiceRq);
            if (shakalServiceRs == null) {
                continue;
            }

            switch (shakalServiceRs.getShakalServiceTask()) {
                case SEND_TEXT -> sender.sendText(chatId, shakalServiceRs);
                case SEND_TEXT_DELETE_KEYBOARD -> sender.sendTextMessage(chatId, shakalServiceRs.getMessageJson().getTextMessage(), ParseMode.Markdown, new ReplyKeyboardRemove());
                case SEND_KEYBOARD -> sender.sendReplyKeyboard(chatId, shakalServiceRs);
                case SEND_DICE -> sender.sendDice(chatId, shakalServiceRs);
            }
        }

        userThreads.removeThread(chatId);
    }
}
