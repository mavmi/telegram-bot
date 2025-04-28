package mavmi.telegram_bot.rocketchat.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.telegram_bot_starter.userThread.UserThreads;
import mavmi.telegram_bot.rocketchat.mapper.RequestsMapper;
import mavmi.telegram_bot.rocketchat.service.RocketService;
import mavmi.telegram_bot.rocketchat.telegramBot.client.RocketTelegramBotSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RocketUserThreads extends UserThreads<RocketUserThread> {

    private final RocketTelegramBotSender sender;
    private final RocketService rocketService;
    private final RequestsMapper requestsMapper;

    @Override
    public void add(Update update) {
        Message message = update.message();
        if (message == null) {
            return;
        }

        long chatId = message.chat().id();
        RocketUserThread userThread = (RocketUserThread) tgIdToUserThread.get(chatId);

        if (userThread == null) {
            userThread = new RocketUserThread(this, sender, rocketService, requestsMapper, chatId);
            tgIdToUserThread.put(chatId, userThread);
            userThread.add(update);
            Thread.ofVirtual().start(userThread);
        } else {
            userThread.add(update);
        }
    }
}
