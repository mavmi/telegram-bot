package mavmi.telegram_bot.water_stuff.telegramBot.userThread;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.telegram_bot_starter.userThread.UserThread;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.water_stuff.mapper.RequestsMapper;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.WaterService;
import mavmi.telegram_bot.water_stuff.telegramBot.client.WaterTelegramBotSender;

import java.util.ArrayDeque;
import java.util.Queue;

@Slf4j
@RequiredArgsConstructor
public class WaterStuffUserThread implements UserThread {

    private final WaterStuffUserThreads userThreads;
    private final UserCachesProvider userCachesProvider;
    private final RequestsMapper requestsMapper;
    private final WaterService waterStuffService;
    private final WaterTelegramBotSender sender;
    private final long chatId;
    private final Queue<Update> updateQueue = new ArrayDeque<>();

    @Override
    public void add(Update update) {
        updateQueue.add(update);
    }

    @Override
    public void run() {
        try {
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

                waterStuffService.handleRequest(waterStuffServiceRq);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            userThreads.removeThread(chatId);
            userCachesProvider.clean();
        }
    }
}
