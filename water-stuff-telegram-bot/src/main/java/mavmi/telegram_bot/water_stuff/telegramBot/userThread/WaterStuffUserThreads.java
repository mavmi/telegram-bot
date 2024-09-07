package mavmi.telegram_bot.water_stuff.telegramBot.userThread;

import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.telegramBot.userThread.UserThreads;
import mavmi.telegram_bot.water_stuff.mapper.RequestsMapper;
import mavmi.telegram_bot.water_stuff.service.water_stuff.WaterStuffDirectService;
import mavmi.telegram_bot.water_stuff.telegramBot.WaterStuffTelegramBotSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaterStuffUserThreads extends UserThreads<WaterStuffUserThread> {

    private final RequestsMapper requestsMapper;
    private final WaterStuffDirectService waterStuffService;
    private final WaterStuffTelegramBotSender sender;

    @Override
    public void add(Update update) {
        long chatId;
        if (update.message() != null) {
            chatId = update.message().chat().id();
        } else {
            chatId = update.callbackQuery().from().id();
        }

        WaterStuffUserThread userThread = (WaterStuffUserThread) tgIdToUserThread.get(chatId);

        if (userThread == null) {
            userThread = new WaterStuffUserThread(this, requestsMapper, waterStuffService, sender, chatId);
            tgIdToUserThread.put(chatId, userThread);
            userThread.add(update);
            new Thread(userThread).start();
        } else {
            userThread.add(update);
        }
    }
}
