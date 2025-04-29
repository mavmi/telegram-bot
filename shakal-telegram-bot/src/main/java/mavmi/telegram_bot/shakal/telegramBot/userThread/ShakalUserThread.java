package mavmi.telegram_bot.shakal.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.telegram_bot_starter.userThread.UserThread;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.shakal.mapper.RequestsMapper;
import mavmi.telegram_bot.shakal.service.ShakalService;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.telegramBot.client.ShakalTelegramBotSender;

import java.util.ArrayDeque;
import java.util.Queue;

@Slf4j
@RequiredArgsConstructor
public class ShakalUserThread implements UserThread {

    private final ShakalUserThreads userThreads;
    private final UserCachesProvider userCachesProvider;
    private final RequestsMapper requestsMapper;
    private final ShakalService shakalService;
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
            shakalService.handleRequest(shakalServiceRq);
        }

        userThreads.removeThread(chatId);
        userCachesProvider.clean();
    }
}
