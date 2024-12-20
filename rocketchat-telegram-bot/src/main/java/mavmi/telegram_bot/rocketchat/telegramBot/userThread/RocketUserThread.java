package mavmi.telegram_bot.rocketchat.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.telegramBot.userThread.UserThread;
import mavmi.telegram_bot.rocketchat.mapper.RequestsMapper;
import mavmi.telegram_bot.rocketchat.service.RocketService;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.telegramBot.client.RocketTelegramBotSender;

import java.util.ArrayDeque;
import java.util.Queue;

@Slf4j
@RequiredArgsConstructor
public class RocketUserThread implements UserThread {

    private final RocketUserThreads userThreads;
    private final RocketTelegramBotSender sender;
    private final RocketService rocketService;
    private final RequestsMapper requestsMapper;
    private final long chatId;
    private final Queue<Update> updateQueue = new ArrayDeque<>();

    @Override
    public void add(Update update) {
        updateQueue.add(update);
    }

    @Override
    @SneakyThrows
    public void run() {
        while (!updateQueue.isEmpty()) {
            try {
                Message message = updateQueue.remove().message();
                RocketchatServiceRq rocketchatServiceRq = requestsMapper.telegramRequestToRocketchatServiceRequest(message);
                rocketService.handleRequest(rocketchatServiceRq);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        userThreads.removeThread(chatId);
    }
}
